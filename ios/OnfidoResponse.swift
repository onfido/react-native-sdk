//
//  OnfidoResponse.swift
//
//  Copyright © 2016-2023 Onfido. All rights reserved.
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
    var reactNfcMediaId: String? { get }
}

protocol ReactFaceResult {
    var id: String { get }
    var variant: FaceResultVariant { get }
}

extension DocumentSideResult: ReactDocumentSideResult {}
extension DocumentResult: ReactDocumentResult {
    var reactFront: ReactDocumentSideResult { front }
    var reactBack: ReactDocumentSideResult? { back }
    var reactNfcMediaId: String? { nfcMediaId }
}

extension FaceResult: ReactFaceResult {}

func createResponse(_ results: [OnfidoResult]) -> [String: [String: Any]] {
    let document: DocumentResult? = results.compactMap { result in
        guard case let .document(documentResult) = result else { return nil }
        return documentResult
    }.first

    let face: FaceResult? = results.compactMap { result in
        guard case let .face(faceResult) = result else { return nil }
        return faceResult
    }.first

    return createResponse(document: document, face: face)
}

// TODO: Refactor to Encodable
func createResponse(
    document: ReactDocumentResult? = nil,
    face: ReactFaceResult? = nil
) -> [String: [String: Any]] {
    var response = [String: [String: Any]]()

    if let documentResponse = document {
        response["document"] = ["front": ["id": documentResponse.reactFront.id]]
        if let backId = documentResponse.reactBack?.id,
           backId != documentResponse.reactFront.id
        {
            response["document"]?["back"] = ["id": documentResponse.reactBack?.id]
        }

        if let nfcId = documentResponse.reactNfcMediaId
        {
            response["document"]?["nfcMediaId"] = ["id": nfcId]
        }
    }

    if let faceResponse = face {
        let faceVariant: String = {
            switch faceResponse.variant {
            case .photo:
                return "PHOTO"
            case .video:
                return "VIDEO"
            case .motion:
                return "MOTION"
            }
            }()  

        response["face"] = ["id": faceResponse.id, "variant": faceVariant]
    }

    return response
}
