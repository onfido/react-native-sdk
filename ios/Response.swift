//
//  Response.swift
//  OnfidoSdk
//
//  Created by Santana, Luis on 3/5/20.
//  Copyright © 2020 Onfido. All rights reserved.
//

import Foundation
import Onfido

func createResponse(_ results: [OnfidoResult], faceVariant: String?) -> [String: [String: Any]] {
  var RNResponse = [String: [String: Any]]()

  let document: [OnfidoResult]? = results.filter({ result in
    if case OnfidoResult.document = result { return true }
    return false;
  });

  let front: OnfidoResult? = document?.first
  let back: OnfidoResult? = document?.last

  let face: OnfidoResult? = results.filter({ result in
    if case OnfidoResult.face = result { return true }
    return false
  }).first

  if let documentUnwrappedFront = front, case OnfidoResult.document(let documentResponseFront) = documentUnwrappedFront {
    RNResponse["document"] = ["front": ["id": documentResponseFront.id]]
    if let documentUnwrappedBack = back, case OnfidoResult.document(let documentResponseBack) = documentUnwrappedBack {
      if (documentResponseBack.id != documentResponseFront.id) {
        RNResponse["document"]?["back"] = ["id": documentResponseBack.id]
      }
    }
  }

  if let faceUnwrapped = face, case OnfidoResult.face(let faceResponse) = faceUnwrapped {
    RNResponse["face"] = ["id": faceResponse.id, "variant": faceVariant!]
  }

  return RNResponse
}
