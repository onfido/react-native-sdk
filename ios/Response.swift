//
//  Response.swift
//  OnfidoSdk
//
//  Created by Santana, Luis on 3/5/20.
//  Copyright © 2020 Onfido. All rights reserved.
//

import Foundation
import Onfido

// 📝 Protocols are for testing purposes since SDK types are final
protocol ReactDocumentSideResult {
    var id: String { get }
}

protocol ReactDocumentResult {
    var reactFront: ReactDocumentSideResult { get }
    var reactBack: ReactDocumentSideResult? { get }
}

protocol ReactFaceResult {
    var id: String { get }
}

extension DocumentSideResult: ReactDocumentSideResult {}
extension DocumentResult: ReactDocumentResult {
    var reactFront: ReactDocumentSideResult { front }
    var reactBack: ReactDocumentSideResult? { back }
}

extension FaceResult: ReactFaceResult {}

func createResponse(_ results: [OnfidoResult], faceVariant: String?) -> [String: [String: Any]] {
    let document: DocumentResult? = results.compactMap { result in
        guard case let .document(documentResult) = result else { return nil }
        return documentResult
    }.first
    let face: FaceResult? = results.compactMap { result in
        guard case let .face(faceResult) = result else { return nil }
        return faceResult
    }.first
    return createResponse(document: document, face: face, faceVariant: faceVariant)
}

func createResponse(
    document: ReactDocumentResult? = nil,
    face: ReactFaceResult? = nil,
    faceVariant: String? = nil
) -> [String: [String: Any]] {
    var response = [String: [String: Any]]()

    if let documentResponse = document {
        response["document"] = ["front": ["id": documentResponse.reactFront.id]]
        if let backId = documentResponse.reactBack?.id,
           backId != documentResponse.reactFront.id
        {
            response["document"]?["back"] = ["id": documentResponse.reactBack?.id]
        }
    }

    if let faceResponse = face {
        var faceResponse = ["id": faceResponse.id]

        if let faceVariant = faceVariant {
            faceResponse["variant"] = faceVariant
        }

        response["face"] = faceResponse
    }

    return response
}
