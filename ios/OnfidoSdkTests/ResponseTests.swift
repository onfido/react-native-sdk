//
//  ResponseTest.swift
//  OnfidoSdkTests
//
//  Created by Santana, Luis on 3/5/20.
//  Copyright © 2020 Onfido. All rights reserved.
//

import Foundation
import Onfido
import XCTest
@testable import OnfidoSdk

class ResponseTests: XCTestCase {

    func testResponseOfSingleSidedDocumentCheck() {
        let json = """
        {
            "id": "single-side-test",
            "href": "not-nil",
            "created_at": 1583534290,
            "file_name": "not-nil.png",
            "file_type": "png",
            "file_size": 282123,
            "side": "front",
            "type": "passport"
        }
        """.data(using: .utf8)!
        
        let onfidoDocument = try! JSONDecoder().decode(DocumentResult.self, from: json)
        
        let response:[String: [String: Any]] = createResponse([OnfidoResult.document(onfidoDocument)], faceVariant: nil)

        guard let document = response["document"] as? [String: [String: String]] else {
            XCTFail()
            return
        }
        
        XCTAssert(document["front"]?["id"] == "single-side-test")
        XCTAssertNil(document["back"])
        XCTAssertNil(response["face"])

    }
    
    func testResponseOfDoubleSidedDocumentCheck() {
        let jsonFront = """
        {
            "id": "double-side-test-1",
            "href": "not-nil",
            "created_at": 1583534290,
            "file_name": "not-nil.png",
            "file_type": "png",
            "file_size": 282123,
            "side": "front",
            "type": "driving-licence"
        }
        """.data(using: .utf8)!
        
        let jsonBack = """
        {
            "id": "double-side-test-2",
            "href": "not-nil",
            "created_at": 1583534290,
            "file_name": "not-nil.png",
            "file_type": "png",
            "file_size": 282123,
            "side": "back",
            "type": "driving-licence"
        }
        """.data(using: .utf8)!
        
        let documentFront = try! JSONDecoder().decode(DocumentResult.self, from: jsonFront)
        let documentBack = try! JSONDecoder().decode(DocumentResult.self, from: jsonBack)
        
        let response:[String: [String: Any]] = createResponse([OnfidoResult.document(documentFront), OnfidoResult.document(documentBack)], faceVariant: nil)

        guard let document = response["document"] as? [String: [String: String]] else {
            XCTFail()
            return
        }
        
        XCTAssert(document["front"]?["id"] == "double-side-test-1")
        XCTAssert(document["back"]?["id"] == "double-side-test-2")
        XCTAssertNil(response["face"])
    }
    
    func testResponseOfFaceCheck() {
        let json = """
        {
            "id": "face-test",
            "href": "not-nil",
            "created_at": 1583534290,
            "file_name": "not-nil.png",
            "file_type": "png",
            "file_size": 282123,
        }
        """.data(using: .utf8)!
        
        let onfidoDocument = try! JSONDecoder().decode(FaceResult.self, from: json)
        
        let response:[String: [String: Any]] = createResponse([OnfidoResult.face(onfidoDocument)], faceVariant: "PHOTO")

        guard let face = response["face"] as? [String: String] else {
            XCTFail()
            return
        }
        
        XCTAssert(face["id"] == "face-test")
        XCTAssert(face["variant"] == "PHOTO")
        XCTAssertNil(response["document"])
    }
}

