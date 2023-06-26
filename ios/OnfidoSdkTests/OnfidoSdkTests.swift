//
//  OnfidoSdkTests.swift
//  OnfidoSdkTests
//
//  Created by Marsh, Jae on 4/7/20.
//  Copyright © 2020 Onfido. All rights reserved.
//

import XCTest

import Onfido
@testable import OnfidoSdk

class OnfidoSdkTests : XCTestCase {

    func testLoadColorDefaults() throws {
        let expectedPrimaryColor = UIColor(red: 53/255.0, green: 63/255.0, blue: 244/255.0, alpha: 1.0)
        let appearancePublic = try loadAppearancePublicFromFile(filePath: "colorsFileDoesNotExist.json")!

        XCTAssertEqual( appearancePublic.primaryColor, expectedPrimaryColor )
    }
    
    func testLoadColorFile() throws {
        let appearanceFilePath = String(#file[...#file.lastIndex(of: "/")!] + "colors.json")
        let appearance = try loadAppearancePublicFromFile(filePath: appearanceFilePath)!

        XCTAssertEqual(appearance.primaryColor, UIColor(red: 1, green: 0, blue: 0, alpha: 1.0))
        XCTAssertEqual(appearance.primaryBackgroundPressedColor, UIColor(red: 0, green: 0, blue: 1, alpha: 1.0))
        XCTAssertEqual(appearance.primaryTitleColor, UIColor(red: 0, green: 1, blue: 0, alpha: 1.0))
        XCTAssertEqual(appearance.secondaryTitleColor, UIColor(red: 1, green: 0, blue: 0, alpha: 1.0))
        XCTAssertEqual(appearance.secondaryBackgroundPressedColor, UIColor(red: 1, green: 0, blue: 0, alpha: 1.0))
        XCTAssertEqual(appearance.fontFamilyBody, "")
        XCTAssertEqual(appearance.buttonCornerRadius, 12)
        XCTAssertEqual(appearance.supportDarkMode, true)
    }

    func testBuildOnfidoConfigMinimal() throws {
        let appearance = try loadAppearanceFromFile(filePath: "colorsFileDoesNotExist.json")
        let builder = OnfidoConfigBuilder()
        let onfidoMode = try builder.build(
            config: .init(
                sdkToken: "demo",
                workflowRunId: nil,
                flowSteps: .init(captureDocument: .init(
                    .init(countryCode: nil, alpha2CountryCode: nil, docType: nil, allowedDocumentTypes: nil))
                ),
                localisation: nil,
                hideLogo: nil,
                logoCoBrand: nil,
                enableNFC: nil
            ),
            appearance: appearance,
            mediaCallBack: nil
        )

        guard case .classic(let configBuilder) = onfidoMode else {
            fatalError("Expected to receive classic mode")
        }

        let onfidoConfig = try configBuilder.build()
        let configString = String(describing: onfidoConfig)
        XCTAssert(configString.contains("document"))
    }

    func testBuildOnfidoConfigWithoutMotion() throws {
        let appearanceFilePath = String(#file[...#file.lastIndex(of: "/")!] + "colors.json")
        let appearance = try loadAppearanceFromFile(filePath: appearanceFilePath)

        let builder = OnfidoConfigBuilder()
        let onfidoMode = try builder.build(
            config: .init(
                sdkToken: "demo",
                workflowRunId: nil,
                flowSteps: .init(
                    welcome: true,
                    captureDocument: .init(
                        .init(
                            countryCode: "USA",
                            alpha2CountryCode: "US",
                            docType: .drivingLicence,
                            allowedDocumentTypes: nil
                        )
                    ),
                    captureFace: .init(
                        type: .photo,
                        recordAudio: false,
                        motionCaptureFallback: nil,
                        showIntro: nil,
                        manualVideoCapture: nil
                    )
                ),
                localisation: nil,
                hideLogo: nil,
                logoCoBrand: nil,
                enableNFC: nil
            ),
            appearance: appearance,
            mediaCallBack: nil
        )

        guard case .classic(let configBuilder) = onfidoMode else {
            fatalError("Expected to receive classic mode")
        }

        let appearanceRefString = String(describing: appearance)
        let onfidoConfig = try configBuilder.build()
        let configString = String(describing: onfidoConfig)
        XCTAssert(configString.contains("intro"))
        XCTAssert(configString.contains("drivingLicence"))
        XCTAssert(configString.contains("photo"))
        XCTAssert(!configString.contains("video"))
        XCTAssert(configString.contains(appearanceRefString))
    }

    func testBuildOnfidoConfigMotionWithCaptureFallback() throws {
        let appearanceFilePath = String(#file[...#file.lastIndex(of: "/")!] + "colors.json")
        let appearance = try loadAppearanceFromFile(filePath: appearanceFilePath)

        let builder = OnfidoConfigBuilder()
        let onfidoMode = try builder.build(
            config: .init(
                sdkToken: "demo",
                workflowRunId: nil,
                flowSteps: .init(
                    welcome: true,
                    captureDocument: .init(
                        .init(
                            countryCode: "USA",
                            alpha2CountryCode: "US",
                            docType: .drivingLicence,
                            allowedDocumentTypes: nil
                        )
                    ),
                    captureFace: .init(
                        type: .motion,
                        recordAudio: nil,
                        motionCaptureFallback: ["type": "PHOTO"],
                        showIntro: nil,
                        manualVideoCapture: nil
                    )
                ),
                localisation: nil,
                hideLogo: nil,
                logoCoBrand: nil,
                enableNFC: nil
            ),
            appearance: appearance,
            mediaCallBack: nil
        )

        guard case .classic(let configBuilder) = onfidoMode else {
            fatalError("Expected to receive classic mode")
        }

        let appearanceRefString = String(describing: appearance)
        let onfidoConfig = try configBuilder.build()
        let configString = String(describing: onfidoConfig)
        XCTAssert(configString.contains("intro"))
        XCTAssert(configString.contains("drivingLicence"))
        XCTAssert(!configString.contains("photo"))
        XCTAssert(!configString.contains("video"))
        XCTAssert(configString.contains("motion"))
        XCTAssert(configString.contains("MotionStepConfiguration")) //this is for fallback option as per implemented iOS SDK if we only have MotionStepConfiguration for fallback and audio.
        XCTAssert(configString.contains(appearanceRefString))
    }

    func testBuildOnfidoConfigMotionWithAudio() throws {
        let appearanceFilePath = String(#file[...#file.lastIndex(of: "/")!] + "colors.json")
        let appearance = try loadAppearanceFromFile(filePath: appearanceFilePath)

        let builder = OnfidoConfigBuilder()
        let onfidoMode = try builder.build(
            config: .init(
                sdkToken: "demo",
                workflowRunId: nil,
                flowSteps: .init(
                    welcome: true,
                    captureDocument: .init(
                        .init(
                            countryCode: "USA",
                            alpha2CountryCode: "US",
                            docType: .drivingLicence,
                            allowedDocumentTypes: nil
                        )
                    ),
                    captureFace: .init(
                        type: .motion,
                        recordAudio: true,
                        motionCaptureFallback: nil,
                        showIntro: nil,
                        manualVideoCapture: nil
                    )
                ),
                localisation: nil,
                hideLogo: nil,
                logoCoBrand: nil,
                enableNFC: nil
            ),
            appearance: appearance,
            mediaCallBack: nil
        )

        guard case .classic(let configBuilder) = onfidoMode else {
            fatalError("Expected to receive classic mode")
        }

        let appearanceRefString = String(describing: appearance)
        let onfidoConfig = try configBuilder.build()
        let configString = String(describing: onfidoConfig)
        XCTAssert(configString.contains("intro"))
        XCTAssert(configString.contains("drivingLicence"))
        XCTAssert(!configString.contains("photo"))
        XCTAssert(!configString.contains("video"))
        XCTAssert(configString.contains("motion"))
        XCTAssert(configString.contains("MotionStepConfiguration")) //this is for fallback option as per implemented iOS SDK if we only have MotionStepConfiguration for fallback and audio.
        XCTAssert(configString.contains(appearanceRefString))
    }

    func testBuildOnfidoConfigMotionWithAudioAndWithFallBack() throws {
        let appearanceFilePath = String(#file[...#file.lastIndex(of: "/")!] + "colors.json")
        let appearance = try loadAppearanceFromFile(filePath: appearanceFilePath)

        let builder = OnfidoConfigBuilder()
        let onfidoMode = try builder.build(
            config: .init(
                sdkToken: "demo",
                workflowRunId: nil,
                flowSteps: .init(
                    welcome: true,
                    captureDocument: .init(
                        .init(
                            countryCode: "USA",
                            alpha2CountryCode: "US",
                            docType: .drivingLicence,
                            allowedDocumentTypes: nil
                        )
                    ),
                    captureFace: .init(
                        type: .motion,
                        recordAudio: false,
                        motionCaptureFallback: ["type": "PHOTO"],
                        showIntro: false,
                        manualVideoCapture: nil
                    )
                ),
                localisation: nil,
                hideLogo: nil,
                logoCoBrand: nil,
                enableNFC: nil
            ),
            appearance: appearance,
            mediaCallBack: nil
        )

        guard case .classic(let configBuilder) = onfidoMode else {
            fatalError("Expected to receive classic mode")
        }

        let appearanceRefString = String(describing: appearance)
        let onfidoConfig = try configBuilder.build()
        let configString = String(describing: onfidoConfig)
        XCTAssert(configString.contains("intro"))
        XCTAssert(configString.contains("drivingLicence"))
        XCTAssert(!configString.contains("photo"))
        XCTAssert(!configString.contains("video"))
        XCTAssert(configString.contains("motion"))
        XCTAssert(configString.contains("MotionStepConfiguration")) //this is for fallback option as per implemented iOS SDK if we only have MotionStepConfiguration for fallback and audio.
        XCTAssert(configString.contains(appearanceRefString))
    }

    func testBuildOnfidoWithBothDocTypeAndAllowedDocumentsDoctypeAndCountryTakesPriority() throws {
        let appearanceFilePath = String(#file[...#file.lastIndex(of: "/")!] + "colors.json")
        let appearance = try loadAppearanceFromFile(filePath: appearanceFilePath)

        let builder = OnfidoConfigBuilder()
        let onfidoMode = try builder.build(
            config: .init(
                sdkToken: "demo",
                workflowRunId: nil,
                flowSteps: .init(captureDocument: .init(
                    .init(
                        countryCode: "USA",
                        alpha2CountryCode: "US",
                        docType: .drivingLicence,
                        allowedDocumentTypes: [
                            .passport,
                            .nationalIdentityCard,
                            .residencePermit
                        ]
                    )
                )),
                localisation: nil,
                hideLogo: nil,
                logoCoBrand: nil,
                enableNFC: nil
            ),
            appearance: appearance,
            mediaCallBack: nil
        )

        guard case .classic(let configBuilder) = onfidoMode else {
            fatalError("Expected to receive classic mode")
        }

        let onfidoConfig = try configBuilder.build()
        let configString = String(describing: onfidoConfig)
        XCTAssert(configString.contains("DocumentType.drivingLicence"))
        XCTAssert(!configString.contains("DocumentType.passport(config: nil)"))
    }

    func testBuildOnfidoWithFilteredAllowedDocumentsInSelection() throws {
        let appearanceFilePath = String(#file[...#file.lastIndex(of: "/")!] + "colors.json")
        let appearance = try loadAppearanceFromFile(filePath: appearanceFilePath)

        let builder = OnfidoConfigBuilder()
        let onfidoMode = try builder.build(
            config: .init(
                sdkToken: "demo",
                workflowRunId: nil,
                flowSteps: .init(captureDocument: .init(
                    .init(
                        countryCode: nil,
                        alpha2CountryCode: nil,
                        docType: nil,
                        allowedDocumentTypes: [
                            .passport,
                            .nationalIdentityCard,
                            .residencePermit,
                            .drivingLicence
                        ]
                    )
                )),
                localisation: nil,
                hideLogo: nil,
                logoCoBrand: nil,
                enableNFC: nil
            ),
            appearance: appearance,
            mediaCallBack: nil
        )

        guard case .classic(let configBuilder) = onfidoMode else {
            fatalError("Expected to receive classic mode")
        }

        let onfidoConfig = try configBuilder.build()
        let configString = String(describing: onfidoConfig)
        XCTAssert(configString.contains("DocumentType.passport(config: nil)"))
        XCTAssert(configString.contains("DocumentType.drivingLicence(config: nil)"))
        XCTAssert(configString.contains("DocumentType.nationalIdentityCard(config: nil)"))
        XCTAssert(configString.contains("DocumentType.residencePermit(config: nil)"))
    }
}
