import Onfido

public class AppearancePublic: NSObject {
    public let primaryColor: UIColor
    public let primaryTitleColor: UIColor
    public let primaryBackgroundPressedColor: UIColor
    public let supportDarkMode: Bool
    public let interfaceStyle: OnfidoInterfaceStyle
    public let secondaryTitleColor: UIColor
    public let secondaryBackgroundPressedColor: UIColor
    public let bubbleErrorBackgroundColor: UIColor
    public let buttonCornerRadius: CGFloat
    public let fontFamilyTitle: String
    public let fontFamilyBody: String
    public let captureSuccessColors: CaptureSuccessColors
    public let backgroundColor: BackgroundColor

    /// public apperance object with shared with RN integrator
    public init(
        primaryColor: UIColor,
        primaryTitleColor: UIColor,
        primaryBackgroundPressedColor: UIColor,
        secondaryTitleColor: UIColor,
        secondaryBackgroundPressedColor: UIColor,
        bubbleErrorBackgroundColor: UIColor,
        buttonCornerRadius: CGFloat,
        fontFamilyBody: String,
        fontFamilyTitle: String,
        captureSuccessColors: CaptureSuccessColors,
        supportDarkMode: Bool = true,
        interfaceStyle: OnfidoInterfaceStyle = .unspecified,
        backgroundColor: BackgroundColor = .init()
    ) {
        self.primaryColor = primaryColor
        self.primaryTitleColor = primaryTitleColor
        self.secondaryTitleColor = secondaryTitleColor
        self.primaryBackgroundPressedColor = primaryBackgroundPressedColor
        self.secondaryBackgroundPressedColor = secondaryBackgroundPressedColor
        self.bubbleErrorBackgroundColor = bubbleErrorBackgroundColor
        self.buttonCornerRadius = buttonCornerRadius
        self.fontFamilyTitle = fontFamilyTitle
        self.fontFamilyBody = fontFamilyBody
        self.captureSuccessColors = captureSuccessColors
        self.supportDarkMode = supportDarkMode
        self.interfaceStyle = interfaceStyle
        self.backgroundColor = backgroundColor
    }
}

public enum OnfidoInterfaceStyle: Int, Decodable {
    case unspecified = 0
    case light = 1
    case dark = 2
}

/**
 * Load appearance data from a file into an appearance class with public access to it's members.
 * This public class is used for unit tests.  Once this data is added to the native Appearance class,
 * any changes to the data in this class will not affect the native SDK appearance.
 */
public func loadAppearancePublicFromFile(filePath: String?) throws -> AppearancePublic? {
    do {
        let jsonResult: Any
        do {
            guard let path = filePath else { return nil }
            let data = try Data(contentsOf: URL(fileURLWithPath: path), options: .mappedIfSafe)
            jsonResult = try JSONSerialization.jsonObject(with: data, options: .mutableLeaves)
        } catch let e as NSError where e.code == NSFileNoSuchFileError || e.code == NSFileReadNoSuchFileError {
            jsonResult = [String: AnyObject]()
        }

        if let jsonResult = jsonResult as? [String: AnyObject] {
            let primaryColor = jsonResult.getColor(withName: "onfidoPrimaryColor", fallback: .primaryColor)
            let primaryTitleColor = jsonResult.getColor(withName: "onfidoPrimaryButtonTextColor", fallback: .white)
            let primaryBackgroundPressedColor = jsonResult.getColor(withName: "onfidoPrimaryButtonColorPressed", fallback: .primaryButtonColorPressed)
            let secondaryTitleColor = jsonResult.getColor(withName: "secondaryTitleColor", fallback: .secondaryTitleColor)
            let bubbleErrorBackgroundColor = jsonResult.getColor(withName: "bubbleErrorBackgroundColor", fallback: .bubbleErrorBackgroundColor)
            let secondaryBackgroundPressedColor = jsonResult.getColor(withName: "secondaryBackgroundPressedColor", fallback: .secondaryBackgroundPressedColor)
            let supportDarkMode: Bool = jsonResult["onfidoIosSupportDarkMode"] as? Bool ?? true
            let buttonCornerRadius: CGFloat = jsonResult["buttonCornerRadius"] as? CGFloat ?? 12
            let fontFamilyTitle = jsonResult["fontFamilyTitle"] as? String ?? ""
            let fontFamilyBody = jsonResult["fontFamilyBody"] as? String ?? ""
            let captureSuccessColors = CaptureSuccessColors()

            if let captureColors = jsonResult["captureSuccessColors"] as? [String: Any] {
                if let borderColor = captureColors["borderColor"] as? String {
                    captureSuccessColors.borderColor = UIColor.from(hex: borderColor)
                }
                if let tickViewBackground = captureColors["tickViewBackgroundColor"] as? String {
                    captureSuccessColors.tickViewBackgroundColor = UIColor.from(hex: tickViewBackground)
                }
                if let tickViewImageTint = captureColors["tickViewImageTintColor"] as? String {
                    captureSuccessColors.tickViewImageTintColor = UIColor.from(hex: tickViewImageTint)
                }
            }

            let interfaceStyle: OnfidoInterfaceStyle
            if let style = jsonResult["interfaceStyle"] as? String {
                interfaceStyle = .init(style)
            } else {
                interfaceStyle = .unspecified
            }

            let backgroundColor: BackgroundColor
            if
                let color = jsonResult["backgroundColor"] as? [String: String],
                let lightColor = color["light"],
                let darkColor = color["dark"]
            {
                backgroundColor = .init(
                    lightColor: .from(hex: lightColor),
                    darkColor: .from(hex: darkColor)
                )
            } else {
                backgroundColor = .init()
            }

            return AppearancePublic(primaryColor: primaryColor,
                                    primaryTitleColor: primaryTitleColor,
                                    primaryBackgroundPressedColor: primaryBackgroundPressedColor,
                                    secondaryTitleColor: secondaryTitleColor,
                                    secondaryBackgroundPressedColor: secondaryBackgroundPressedColor,
                                    bubbleErrorBackgroundColor: bubbleErrorBackgroundColor,
                                    buttonCornerRadius: buttonCornerRadius,
                                    fontFamilyBody: fontFamilyBody,
                                    fontFamilyTitle: fontFamilyTitle,
                                    captureSuccessColors: captureSuccessColors,
                                    supportDarkMode: supportDarkMode,
                                    interfaceStyle: interfaceStyle,
                                    backgroundColor: backgroundColor)
        } else {
            return nil
        }
    } catch {
        throw NSError(domain: "There was an error setting colors for Appearance: \(error)", code: 0)
    }
}

/**
 * Load appearance data from the specified file.  If the file cannot be loaded, use the default colors.
 */
public func loadAppearanceFromFile(filePath: String?) throws -> Appearance {
    let appearancePublic = try loadAppearancePublicFromFile(filePath: filePath)

    if let appearancePublic = appearancePublic {
        let appearance = Appearance()
        appearance.primaryColor = appearancePublic.primaryColor
        appearance.primaryTitleColor = appearancePublic.primaryTitleColor
        appearance.primaryBackgroundPressedColor = appearancePublic.primaryBackgroundPressedColor
        appearance.supportDarkMode = appearancePublic.supportDarkMode
        appearance.secondaryTitleColor = appearancePublic.secondaryTitleColor
        appearance.secondaryBackgroundPressedColor = appearancePublic.secondaryBackgroundPressedColor
        appearance.bubbleErrorBackgroundColor = appearancePublic.bubbleErrorBackgroundColor
        appearance.buttonCornerRadius = appearancePublic.buttonCornerRadius
        appearance.fontRegular = appearancePublic.fontFamilyBody
        appearance.fontBold = appearancePublic.fontFamilyTitle
        appearance.captureSuccessColors = appearancePublic.captureSuccessColors
        if #available(iOS 12.0, *) {
            appearance.setUserInterfaceStyle(.init(appearancePublic.interfaceStyle))
        }
        appearance.backgroundColor = appearancePublic.backgroundColor
        return appearance
    } else {
        return Appearance.default
    }
}

extension Appearance {
    static let `default`: Appearance = {
        let appearance = Appearance()
        appearance.primaryColor = .primaryColor
        appearance.primaryTitleColor = .white
        appearance.primaryBackgroundPressedColor = .primaryButtonColorPressed
        return appearance
    }()
}

extension UIColor {
    static var primaryColor: UIColor {
        return decideColor(light: UIColor.from(hex: "#353FF4"), dark: UIColor.from(hex: "#3B43D8"))
    }

    static var primaryButtonColorPressed: UIColor {
        return decideColor(light: UIColor.from(hex: "#232AAD"), dark: UIColor.from(hex: "#5C6CFF"))
    }

    static var secondaryTitleColor: UIColor {
        return decideColor(light: UIColor.from(hex: "#353FF4"), dark: UIColor.from(hex: "#3B43D8"))
    }

    static var secondaryBackgroundPressedColor: UIColor {
        return decideColor(light: UIColor.from(hex: "#353FF4"), dark: UIColor.from(hex: "#3B43D8"))
    }

    static var bubbleErrorBackgroundColor: UIColor {
        return decideColor(light: UIColor.from(hex: "#F53636"), dark: UIColor.from(hex: "#F53636"))
    }

    private static func decideColor(light: UIColor, dark: UIColor) -> UIColor {
        #if XCODE11
            guard #available(iOS 13.0, *) else {
                return light
            }
            return UIColor { collection -> UIColor in
                collection.userInterfaceStyle == .dark ? dark : light
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

// MARK: - Private Helpers

@available(iOS 12.0, *)
private extension UIUserInterfaceStyle {
    init(_ onfidoInterfaceStyle: OnfidoInterfaceStyle) {
        switch onfidoInterfaceStyle {
        case .dark:
            self = .dark
        case .light:
            self = .light
        case .unspecified:
            self = .unspecified
        }
    }
}

private extension OnfidoInterfaceStyle {
    init(_ stringRepresentation: String) {
        switch stringRepresentation {
        case "light":
            self = .light
        case "dark":
            self = .dark
        default:
            self = .unspecified
        }
    }
}
