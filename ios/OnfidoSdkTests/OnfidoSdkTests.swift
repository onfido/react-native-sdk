//
//  OnfidoSdkTests.swift
//  OnfidoSdkTests
//
//  Created by Marsh, Jae on 4/7/20.
//  Copyright © 2020 Onfido. All rights reserved.
//

import XCTest

import OnfidoSdk

class OnfidoSdkTests : XCTestCase {

    func testLoadColorDefaults() throws {
        let expectedPrimaryColor = UIColor(red: 53/255.0, green: 63/255.0, blue: 244/255.0, alpha: 1.0)
        let appearancePublic = try loadAppearancePublicFromFile(filePath: "colorsFileDoesNotExist.json")!

        XCTAssertEqual( appearancePublic.primaryColor, expectedPrimaryColor )
    }
    
    func testLoadColorFile() throws {
        let appearanceFilePath = String(#file[...#file.lastIndex(of: "/")!] + "colors.json")
        let appearance = try loadAppearancePublicFromFile(filePath: appearanceFilePath)!

        XCTAssertEqual( appearance.primaryColor, UIColor(red: 1, green: 0, blue: 0, alpha: 1.0) )
        XCTAssertEqual( appearance.primaryBackgroundPressedColor, UIColor(red: 0, green: 0, blue: 1, alpha: 1.0) )
        XCTAssertEqual( appearance.primaryTitleColor, UIColor(red: 0, green: 1, blue: 0, alpha: 1.0) )
        XCTAssertEqual( appearance.supportDarkMode, true )
    }

    func testBuildOnfidoConfigMinimal() throws {
        let appearance = try loadAppearanceFromFile(filePath: "colorsFileDoesNotExist.json")
        let config: NSDictionary = [
            "sdkToken" : "abc123",
            "flowSteps" : [
                "captureDocument": [:]
            ]
        ]
        
        let onfidoConfigBuilder = try buildOnfidoConfig(config: config, appearance: appearance)
        let builtOnfidoConfig = try onfidoConfigBuilder.build()
        let configString = String(describing: builtOnfidoConfig)
        XCTAssert( configString.contains("document") )
    }

    func testBuildOnfidoConfigWithAllParams() throws {
        let appearanceFilePath = String(#file[...#file.lastIndex(of: "/")!] + "colors.json")
        let appearance = try loadAppearanceFromFile(filePath: appearanceFilePath)
        let appearanceRefString = String(describing: appearance)
        let config: NSDictionary = [
            "sdkToken" : "abc123",
            "flowSteps" : [
                "welcome": NSNumber(value:true),
                "captureDocument": [
                    "docType": "DRIVING_LICENCE",
                    "countryCode": "USA"
                ],
                "captureFace": [
                    "type": "PHOTO",
                ]
            ]
        ]
        
        let onfidoConfigBuilder = try buildOnfidoConfig(config: config, appearance: appearance)
        let builtOnfidoConfig = try onfidoConfigBuilder.build()
        
        let configString = String(describing: builtOnfidoConfig)
        XCTAssert( configString.contains("intro"))
        XCTAssert( configString.contains("drivingLicence") )
        XCTAssert( configString.contains("photo") )
        XCTAssert( !configString.contains("video") )
        XCTAssert( configString.contains(appearanceRefString) )
    }
}
