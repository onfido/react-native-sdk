//
//  OnfidoPluginConfig.swift
//
//  Copyright © 2016-2024 Onfido. All rights reserved.
//

import Foundation

struct OnfidoPluginConfig: Codable {
    let sdkToken: String
    let workflowRunId: String?
    let flowSteps: OnfidoFlowSteps?
    let localisation: OnfidoLocalisation?
    let hideLogo: Bool?
    let logoCoBrand: Bool?
    let disableNFC: Bool?
    let disableMobileSdkAnalytics: Bool?
}

struct OnfidoFlowSteps: Codable {
    var welcome: Bool?
    var proofOfAddress: Bool?
    var captureDocument: OnfidoCaptureDocument?
    var captureFace: OnfidoCaptureFace?
}

struct OnfidoCaptureDocument: Codable {
    let countryCode: String?
    let alpha2CountryCode: String?
    let docType: OnfidoDocumentType?
    let allowedDocumentTypes: [OnfidoDocumentType]?
}

struct OnfidoCaptureFace: Codable {
    let type: OnfidoCaptureType
    let recordAudio: Bool?
    let showIntro: Bool?
    let manualVideoCapture: Bool?
}

struct OnfidoLocalisation: Codable {
    let stringsFileName: String?
    
    enum CodingKeys: String, CodingKey {
        case stringsFileName = "ios_strings_file_name"
    }
}

enum OnfidoDocumentType: String, Codable {
    case passport = "PASSPORT"
    case drivingLicence = "DRIVING_LICENCE"
    case nationalIdentityCard = "NATIONAL_IDENTITY_CARD"
    case residencePermit = "RESIDENCE_PERMIT"
    case visa = "VISA"
    case workPermit = "WORK_PERMIT"
    case generic = "GENERIC"
}

enum OnfidoCaptureType: String, Codable {
    case photo = "PHOTO"
    case video = "VIDEO"
    case motion = "MOTION"
}

struct OnfidoAppearanceConfig: Codable {
    let primaryColorHex: String?
    let primaryButtonTextColorHex: String?
    let primaryButtonColorPressedHex: String?

    enum CodingKeys: String, CodingKey {
        case primaryColorHex = "onfidoPrimaryColor"
        case primaryButtonTextColorHex = "onfidoPrimaryButtonTextColor"
        case primaryButtonColorPressedHex = "onfidoPrimaryButtonColorPressed"
    }
}
