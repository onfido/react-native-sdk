//
//  CallbackReceiver.swift
//
//  Copyright © 2016-2025 Onfido. All rights reserved.
//

import Onfido
import Foundation

final class CallbackReceiver {
    private let onMediaCallback: (([String: Any]) -> Void)?
    
    init(withCallback callback: (([String: Any]) -> Void)?) {
        onMediaCallback = callback
    }
}

extension CallbackReceiver: MediaCallback {
    func onMediaCaptured(result: MediaResult) {
        var dictionary: [String: Any] = [:]

        switch result {
        case let documentResult as MediaDocumentResult:
            dictionary[Keys.MediaCallback.captureType] = Keys.CaptureType.document
            dictionary[Keys.MediaCallback.documentSide] = documentResult.metadata.side
            dictionary[Keys.MediaCallback.documentType] = documentResult.metadata.type
            dictionary[Keys.MediaCallback.documentIssuingCountry] = documentResult.metadata.issuingCountry ?? ""
            send(dictionary: dictionary, mediaFile: documentResult.file)

        case let livenessReult as LivenessResult:
            dictionary[Keys.MediaCallback.captureType] = Keys.CaptureType.video
            send(dictionary: dictionary, mediaFile: livenessReult.file)

        case let selfieResult as SelfieResult:
            dictionary[Keys.MediaCallback.captureType] = Keys.CaptureType.face
            send(dictionary: dictionary, mediaFile: selfieResult.file)

        default:
            return
        }
    }

    private func send(dictionary: [String: Any], mediaFile file: MediaFile) {
        let fileData = getArrayOfBytesFromImage(data: file.fileData as NSData).description

        var newDict = dictionary
        newDict[Keys.MediaCallback.fileName] = file.fileName
        newDict[Keys.MediaCallback.fileData] = fileData
        newDict[Keys.MediaCallback.fileType] = file.fileType

        guard let onMediaCallback = onMediaCallback else {
            assertionFailure("No onMediaCallback registered")
            return
        }
        onMediaCallback(newDict)
    }

    // TODO: Temporary. Removed when introducing breaking change to return Base64 encoded string.
    // https://stackoverflow.com/a/65265130
    private func getArrayOfBytesFromImage(data: NSData) -> Array<Int8> {
        let count = data.length / MemoryLayout<Int8>.size
        var bytes = [Int8](repeating: 0, count: count)
        data.getBytes(&bytes, length:count * MemoryLayout<Int8>.size)

        var byteArray:Array = Array<Int8>()
        for i in 0 ..< count {
            byteArray.append(bytes[i])
        }
        return byteArray
    }
}
