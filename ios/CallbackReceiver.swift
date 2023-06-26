//
//  CallbackReceiver.swift
//
//  Copyright © 2016-2023 Onfido. All rights reserved.
//

import Onfido

class CallbackReceiver {
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
        var newDict = dictionary
        newDict[Keys.MediaCallback.fileName] = file.fileName
        newDict[Keys.MediaCallback.fileData] = file.fileData
        newDict[Keys.MediaCallback.fileType] = file.fileType

        guard let onMediaCallback = onMediaCallback else {
            assertionFailure("No onMediaCallback registered")
            return
        }
        onMediaCallback(newDict)
    }
}
