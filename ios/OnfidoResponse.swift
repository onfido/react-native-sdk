//
//  OnfidoResponse.swift
//
//  Copyright © 2016-2025 Onfido. All rights reserved.
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
    var reactTypeSelected: String { get }
    var reactCountrySelected: String? { get }
    var reactNfcMediaId: String? { get }
}

protocol ReactFaceResult {
    var id: String { get }
    var variant: FaceResultVariant { get }
}

protocol ReactProofOfAddressResult {
    var reactFront: ReactProofOfAddressSideResult { get }
    var reactBack: ReactProofOfAddressSideResult? { get }
    var reactType: String { get }
}

protocol ReactProofOfAddressSideResult {
    var id: String { get }
    var type: String? { get }
}

extension DocumentSideResult: ReactDocumentSideResult {}
extension DocumentResult: ReactDocumentResult {
    var reactFront: ReactDocumentSideResult { front }
    var reactBack: ReactDocumentSideResult? { back }
    var reactTypeSelected: String { typeSelected }
    var reactCountrySelected: String? { countrySelected }
    var reactNfcMediaId: String? { nfcMediaId }
}

extension FaceResult: ReactFaceResult {}

extension ProofOfAddressSideResult: ReactProofOfAddressSideResult {}
extension ProofOfAddressResult: ReactProofOfAddressResult {
    var reactFront: ReactProofOfAddressSideResult { front }
    var reactBack: ReactProofOfAddressSideResult? { back }
    var reactType: String { type }
}

func createResponse(_ results: [OnfidoResult]) -> [String: [String: Any]] {
    let document: DocumentResult? = results.compactMap { result in
        guard case let .document(documentResult) = result else { return nil }
        return documentResult
    }.first

    let face: FaceResult? = results.compactMap { result in
        guard case let .face(faceResult) = result else { return nil }
        return faceResult
    }.first

    let poa: ProofOfAddressResult? = results.compactMap { result in
        guard case let .proofOfAddress(proofOfAddressResult) = result else { return nil }
        return proofOfAddressResult
    }.first

    return createResponse(document: document, face: face)
}

// TODO: Refactor to Encodable
func createResponse(
    document: ReactDocumentResult? = nil,
    face: ReactFaceResult? = nil,
    proofOfAddress: ReactProofOfAddressResult? = nil
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

    if let poaResponse = proofOfAddress {
        response["proofOfAddress"] = ["front": ["id": poaResponse.reactFront.id]]

        if let backId = poaResponse.reactBack?.id,
           backId != poaResponse.reactFront.id
        {
            response["proofOfAddress"]?["back"] = ["id": backId]
        }
        response["proofOfAddress"]?["type"] = poaResponse.reactType
    }

    return response
}
