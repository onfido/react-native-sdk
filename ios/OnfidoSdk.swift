//
//  OnfidoSdk.swift
//  OnfidoSdk
//
//  Created by Santana, Luis on 2/27/20.
//  Copyright © 2020 Onfido. All rights reserved.
//

import Foundation
import Onfido

public class AppearancePublic: NSObject {

    public let primaryColor: UIColor
    public let primaryTitleColor: UIColor
    public let primaryBackgroundPressedColor: UIColor
    public let supportDarkMode: Bool

    public init(
        primaryColor: UIColor,
        primaryTitleColor: UIColor,
        primaryBackgroundPressedColor: UIColor,
        supportDarkMode: Bool = true) {
        self.primaryColor = primaryColor
        self.primaryTitleColor = primaryTitleColor
        self.primaryBackgroundPressedColor = primaryBackgroundPressedColor
        self.supportDarkMode = supportDarkMode
    }
}

/**
 * Load appearance data from a file into an appearance class with public access to it's members.
 * This public class is used for unit tests.  Once this data is added to the native Appearance class,
 * any changes to the data in this class will not affect the native SDK appearance.
 */
public func loadAppearancePublicFromFile(filePath: String?) throws -> AppearancePublic? {

  do {
      let jsonResult:Any
      do {
        guard let path = filePath else { return nil }
        let data = try Data(contentsOf: URL(fileURLWithPath: path), options: .mappedIfSafe)
        jsonResult = try JSONSerialization.jsonObject(with: data, options: .mutableLeaves)
      } catch let e as NSError where e.code == NSFileNoSuchFileError || e.code == NSFileReadNoSuchFileError {
        jsonResult = Dictionary<String, AnyObject>()
      }
      if let jsonResult = jsonResult as? Dictionary<String, AnyObject> {
          let primaryColor: UIColor = (jsonResult["onfidoPrimaryColor"] == nil)
                  ? UIColor.primaryColor : UIColor.from(hex: jsonResult["onfidoPrimaryColor"] as! String)
          let primaryTitleColor: UIColor = (jsonResult["onfidoPrimaryButtonTextColor"] == nil)
                  ? UIColor.white : UIColor.from(hex: jsonResult["onfidoPrimaryButtonTextColor"] as! String)
          let primaryBackgroundPressedColor: UIColor = (jsonResult["onfidoPrimaryButtonColorPressed"] == nil)
                  ? UIColor.primaryButtonColorPressed : UIColor.from(hex: jsonResult["onfidoPrimaryButtonColorPressed"] as! String)
          let supportDarkMode: Bool = (jsonResult["onfidoIosSupportDarkMode"] == nil)
                  ? true : jsonResult["onfidoIosSupportDarkMode"] as! Bool


          let appearancePublic = AppearancePublic(
                  primaryColor: primaryColor,
                  primaryTitleColor: primaryTitleColor,
                  primaryBackgroundPressedColor: primaryBackgroundPressedColor,
                  supportDarkMode: supportDarkMode
          )
          return appearancePublic
      } else {
          return nil
    }
  } catch let error {
      throw NSError(domain: "There was an error setting colors for Appearance: \(error)", code: 0)
  }
}
/**
 * Load appearance data from the specified file.  If the file cannot be loaded, use the default colors.
 */
public func loadAppearanceFromFile(filePath: String?) throws -> Appearance {
    let appearancePublic = try loadAppearancePublicFromFile(filePath: filePath)

    if let appearancePublic = appearancePublic {
        return Appearance(
            primaryColor: appearancePublic.primaryColor,
            primaryTitleColor: appearancePublic.primaryTitleColor,
            primaryBackgroundPressedColor: appearancePublic.primaryBackgroundPressedColor,
            supportDarkMode: appearancePublic.supportDarkMode
        )
    } else {
        return Appearance.default;
    }
}

public func buildOnfidoConfig(config:NSDictionary, appearance: Appearance) throws -> Onfido.OnfidoConfig.Builder {
  let sdkToken:String = config["sdkToken"] as! String
  let flowSteps:NSDictionary? = config["flowSteps"] as? NSDictionary
  let captureDocument:NSDictionary? = flowSteps?["captureDocument"] as? NSDictionary
  let captureFace:NSDictionary? = flowSteps?["captureFace"] as? NSDictionary

  var onfidoConfig = OnfidoConfig.builder()
    .withSDKToken(sdkToken)
    .withAppearance(appearance)

  if let localisationConfig = config["localisation"] as? NSDictionary, let file = localisationConfig["ios_strings_file_name"] as? String {
    onfidoConfig = onfidoConfig.withCustomLocalization(andTableName: file)
  }

  if flowSteps?["welcome"] as? Bool == true {
    onfidoConfig = onfidoConfig.withWelcomeStep()
  }

  if let docType = captureDocument?["docType"] as? String, let countryCode = captureDocument?["countryCode"] as? String {
    switch docType {
      case "PASSPORT":
        onfidoConfig = onfidoConfig.withDocumentStep(ofType: .passport(config: PassportConfiguration()))
      case "DRIVING_LICENCE":
        onfidoConfig = onfidoConfig.withDocumentStep(ofType: .drivingLicence(config: DrivingLicenceConfiguration(country: countryCode)))
      case "NATIONAL_IDENTITY_CARD":
        onfidoConfig = onfidoConfig.withDocumentStep(ofType: .nationalIdentityCard(config: NationalIdentityConfiguration(country: countryCode)))
      case "RESIDENCE_PERMIT":
        onfidoConfig = onfidoConfig.withDocumentStep(ofType: .residencePermit(config: ResidencePermitConfiguration(country: countryCode)))
      case "VISA":
        onfidoConfig = onfidoConfig.withDocumentStep(ofType: .visa(config: VisaConfiguration(country: countryCode)))
      case "WORK_PERMIT":
        onfidoConfig = onfidoConfig.withDocumentStep(ofType: .workPermit(config: WorkPermitConfiguration(country: countryCode)))
      case "GENERIC":
        onfidoConfig = onfidoConfig.withDocumentStep(ofType: .generic(config: GenericDocumentConfiguration(country: countryCode)))
      default:
        throw NSError(domain: "Unsupported document type", code: 0)
    }
  } else if captureDocument != nil {
    onfidoConfig = onfidoConfig.withDocumentStep()
  }

  if let faceVariant = captureFace?["type"] as? String {
    if faceVariant == "VIDEO" {
        onfidoConfig = onfidoConfig.withFaceStep(ofVariant: .video(withConfiguration: VideoStepConfiguration(showIntroVideo: true, manualLivenessCapture: false)))
    } else if faceVariant == "PHOTO" {
      onfidoConfig = onfidoConfig.withFaceStep(ofVariant: .photo(withConfiguration: nil))
    } else {
      throw NSError(domain: "Invalid or unsupported face variant", code: 0)
    }
  }
     return onfidoConfig;
}

@objc(OnfidoSdk)
class OnfidoSdk: NSObject {

  @objc static func requiresMainQueueSetup() -> Bool {
    return false
  }

  @objc func start(_ config: NSDictionary,
                      resolver resolve: @escaping RCTPromiseResolveBlock,
                      rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    DispatchQueue.main.async {
      self.run(withConfig: config, resolver: resolve, rejecter: reject)
    }
  }

  private func run(withConfig config: NSDictionary,
                  resolver resolve: @escaping RCTPromiseResolveBlock,
                  rejecter reject: @escaping RCTPromiseRejectBlock) {

    do {
      let appearanceFilePath = Bundle.main.path(forResource: "colors", ofType: "json")
      let appearance = try loadAppearanceFromFile(filePath: appearanceFilePath)
      let onfidoConfig = try buildOnfidoConfig(config: config, appearance: appearance)
      let builtOnfidoConfig = try onfidoConfig.build()

      //  Copy the face varient from the config since it is not contained in the response:
      let flowSteps:NSDictionary? = config["flowSteps"] as? NSDictionary
      let captureFace:NSDictionary? = flowSteps?["captureFace"] as? NSDictionary
      let faceVariant = captureFace?["type"] as? String

      let onfidoFlow = OnfidoFlow(withConfiguration: builtOnfidoConfig)
        .with(responseHandler: { [weak self] response in
          guard let `self` = self else { return }
          switch response {
            case let .error(error):
              reject("error", "Encountered an error: \(error)", error)
              return;
            case let .success(results):
              resolve(createResponse(results, faceVariant: faceVariant))
              return;
            case .cancel:
              reject("cancel", "User canceled flow", nil)
              return;
            default:
              reject("error", "Unknown error has occured", nil)
              return;
          }
        })

      let onfidoRun = try onfidoFlow.run()
      onfidoRun.modalPresentationStyle = .fullScreen
      UIApplication.shared.windows.first?.rootViewController?.present(onfidoRun, animated: true)
    } catch let error as NSError {
      reject("error", error.domain, error)
      return;
    } catch let error {
      reject("error", "Error running Onfido SDK", error)
      return;
    }
  }
}

extension UIColor {

    static var primaryColor: UIColor {
        return decideColor(light: UIColor.from(hex: "#353FF4"), dark: UIColor.from(hex: "#3B43D8"))
    }

    static var primaryButtonColorPressed: UIColor {
        return decideColor(light: UIColor.from(hex: "#232AAD"), dark: UIColor.from(hex: "#5C6CFF"))
    }

    private static func decideColor(light: UIColor, dark: UIColor) -> UIColor {
        #if XCODE11
        guard #available(iOS 13.0, *) else {
            return light
        }
        return UIColor { (collection) -> UIColor in
            return collection.userInterfaceStyle == .dark ? dark : light
        }
        #else
        return light
        #endif
    }

    static func from(hex: String) -> UIColor {

        let hexString = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        let scanner = Scanner(string: hexString)

        if hexString.hasPrefix("#") {
            scanner.scanLocation = 1
        }

        var color: UInt32 = 0
        scanner.scanHexInt32(&color)

        let mask = 0x000000FF
        let redInt = Int(color >> 16) & mask
        let greenInt = Int(color >> 8) & mask
        let blueInt = Int(color) & mask

        let red = CGFloat(redInt) / 255.0
        let green = CGFloat(greenInt) / 255.0
        let blue = CGFloat(blueInt) / 255.0

        return UIColor(red: red, green: green, blue: blue, alpha: 1.0)
    }
}

extension Appearance {

    static let `default` = Appearance(
            primaryColor: UIColor.primaryColor,
            primaryTitleColor: UIColor.white,
            primaryBackgroundPressedColor: UIColor.primaryButtonColorPressed)
}
