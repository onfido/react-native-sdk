//
//  BridgeUtils.swift
//
//  Copyright © 2016-2025 Onfido. All rights reserved.
//

/*
 NOTE: These are used in junction with RN + Android, to communicate the
 information received to the MediaCallback added in TestApp/App.js
*/

import Foundation

enum Keys {
    enum MediaCallback {
        static let fileData = "fileData"
        static let fileName = "fileName"
        static let fileType = "fileType"
        static let documentSide = "side"
        static let documentType = "type"
        static let documentIssuingCountry = "issuingCountry"
        static let captureType = "captureType"
    }

    enum CaptureType {
        static let document = "DOCUMENT"
        static let face = "FACE"
        static let video = "VIDEO"
    }
}

