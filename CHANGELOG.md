# Change Log

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [NEXT] - 

## [14.6.0] - 2025-04-24

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 32.5.x (up from 32.4.x)
  - Android 22.7.x (up from 22.6.x)

### Fixed:

- Allowed `captureDocument.allowedDocumentTypes` typing to have multiple document types
- Changed `flowSteps` and `Theme` to optional to be consistent with documentation

## [14.5.0] - 2025-04-08

### Changed:

- Updated underlying Onfido native SDK version:
  - Android 22.6.x (up from 22.5.x)

## [14.4.0] - 2025-03-10

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 32.4.x (up from 32.2.x)
  - Android 22.5.x (up from 22.3.x)

## [14.3.0] - 2025-02-04

### Changed:

- Updated underlying Onfido native SDK version:
  - Android 22.3.x (up from 22.2.x)

## [14.2.0] - 2025-01-14

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 32.2.x (up from 32.1.x)
  - Android 22.2.x (up from 22.1.x)

## [14.1.0] - 2024-12-05

### Changed:

- Updated underlying Onfido native SDK version:
  - Android 22.1.x (up from 22.0.x)
  
### Fixed:

- The OnfidoTheme.AUTOMATIC now correctly works on iOS.  

## [14.0.0] - 2024-11-04

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 32.1.x (up from 30.6.x)
  - Android 22.0.x (up from 21.2.x)

- Upgraded to RN 0.73.8
- Updated Kotlin version to 1.9.22
- Updated Java version to 11

## [13.2.0] - 2024-09-10

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 30.6.x (up from 30.5.x)
  - Android 21.2.x (up from 21.1.x)

## [13.1.0] - 2024-08-21

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 30.5.x (up from 30.4.x)
  - Android 21.1.x (up from 21.0.x)

## [13.0.0] - 2024-07-30

### Added:

- Added `nfcOption` parameter for configuring NFC
- Deprecated `disableNFC` parameter (Use `nfcOption` with `OnfidoNFCOptions.DISABLED` value instead)

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 30.4.x (up from 30.3.x)
  - Android 21.0.x (up from 20.5.x)
- Bumped Android target SDK version to 34 (Java version 17 is required to build the project)

## [12.3.0] - 2024-07-04

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 30.3.x (up from 30.2.x)
  - Android 20.5.x (up from 20.4.x)

## [12.2.0] - 2024-06-27

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 30.2.x (up from 30.1.x)
  - Android 20.4.x (up from 20.3.x)

## [12.1.0] - 2024-05-21

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 30.1.x (up from 29.9.x)
  - Android 20.3.x (up from 20.2.x)

## [12.0.0] - 2024-04-24

### Changed:

- Updated the underlying Onfido iOS SDK to version 30.0.x (up from 29.9.x).
  NOTE: This version increased the minimum deployment target to iOS 13
  For more information visit the [Onfido iOS SDK releases on Github](https://github.com/onfido/onfido-ios-sdk/releases)
- Updated the underlying Onfido Android SDK to version 20.2.x (up from 20.1.x).
  For more information visit the [Onfido Android SDK releases on Github](https://github.com/onfido/onfido-android-sdk/releases)
- Motion capture fallback configuration has been removed: Motion is supported on all devices as of 11.0.0
  - If you currently set a `motionCaptureFallback` for `captureFace`, then you should be aware that this configuration is no longer available, so you can safely remove it from your integration code
- The option to set the `bubbleErrorBackgroundColor` as part of the appearance configuration has been removed, as this configuration option is not longer available.
- The flag `supportDarkMode` on the appearance configuration has been removed. Use `interfaceStyle` instead to configure the same behaviour.

## [11.1.0] - 2024-03-06

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 29.9.1 (up from 29.8.x)
  - Android 20.1.x (up from 20.0.x)

## [11.0.0] - 2024-03-06

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 29.8.x (up from 29.7.x)
  - Android 20.0.x (up from 19.6.x)

## [10.7.0] - 2024-02-01

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 29.7.x (up from 29.6.x)
  - Android 19.6.x (up from 19.5.x)

### Fixed:

- Fixed the behaviour when using the configuration option `allowedDocumentTypes`: 
  - The order of steps (and SDK screens) has now been aligned with the native SDKs, for both Android and iOS. The order to be used is: Welcome - Document capture - Proof of address - Face capture - Final screen.
  - The Welcome screen now correctly takes into account the `welcome` (boolean) flag in the configuration.

## [10.6.0] - 2023-12-11

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 29.6.x (up from 29.5.x)
  - Android 19.5.x (up from 19.4.x)

## [10.5.0] - 2023-11-15

### Changed:

- Updated underlying Onfido native SDK version:
  - Android 19.4.x (up from 19.3.x)

### Fixed:

- Fixed OnfidoConfig `disableNFC` definition, as former `enableNFC` was changed.
  NFC is now opt-out, being enabled by default.

## [10.4.0] - 2023-10-10

### Changed:

- Updated underlying Onfido native SDK version:
  - iOS 29.5.x (up from 29.4.x)
  - Android 19.3.x (up from 19.2.x)

### Fixed:

- Fixed missing `captureType` for `MediaResult` objects - on Android

## [10.3.1] - 2023-09-21

### Added:

- Added a helper function `byteArrayStringToBase64` for converting the `fileData` attribute of a `MediaFile`, from a String representation of byte array data, to a Base64 format - in the custom media callbacks use case.

### Changed:

- Updated the `OnfidoMediaResult` type so that `captureType` is visible. This refers to the type of the media capture in each case, which can be `DOCUMENT`, `FACE` or `VIDEO`.
- Updated the Media Callbacks documentation.

## [10.3.0] - 2023-09-11

### Added:

- Updated underlying Onfido native SDK version:
  - iOS 29.4.x (up from 29.3.x)
  - Android 19.2.x (up from 19.1.x)

## [10.2.0] - 2023-09-01

### Changed:

- Changed Face step border success colour in Video Step
- Updated underlying React Native version to 0.72.1 (up from 0.64.3). Please note that this also updates the minimum Node version required to 16.

## [10.1.0] - 2023-08-01

### Added:

- Updated underlying Onfido native SDK version:
  - iOS 29.3.x (up from 29.2.1)
- Changed `primaryColor` description
- Added theme selection to the SDK configuration

### Changed:

- Adjust colour contrast of brackets in Headturn step in motion
- Adjust colour contrast of corners in Alignment step in motion
- Added feedback on UI while video is being recorded on document capture
- Removed OnfidoExtended framework variant

## [10.0.0] - 2023-07-12

### Changed:

- Public: Updated underlying Onfido native SDKs versions:
  - iOS 29.1.x (up from 29.0.x)
  - Android 19.1.x (up from 18.0.x)
- Public: Added Proof of Address configuration

### Fixed:

- Fixed Motion fallback configuration for iOS

## [9.0.0] - 2023-06-26

### Changed:

- Public: Changed how to setup fallbacks for Motion

### Added:

- Public: Added support for RecordingAudio Option as RecordAudio - MOTION:
- Public: Allow Selfie/Video step configuration

## [8.3.0] - 2023-05-02

### Added:

- Public: Added support for Fallback Option OnfidoFaceCaptureOptions - MOTION:

### Fixed:

- Public: Fixed the Workflow result handling crash

## [8.2.0] - 2023-04-12

### Changed:

- Public: Updated underlying Onfido native SDKs versions:
  - iOS 28.3.x (up from 28.1.x)
  - Android 16.3.x (up from 16.1.x)

## [8.1.0] - 2023-03-06

### Changed:

- Public: Updated underlying Onfido native SDKs versions:
  - iOS 28.1.0 (up from 28.0.0)
  - Android 16.1.0 (up from 16.0.1)
- Underlying Onfido native SDKs versions are now less strict and will allow
  patch versions updates

## [8.0.0] - 2023-02-28

### Changed:

- Public: Updated underlying Onfido native SDKs versions:
  - iOS 28.0.0 (up from 27.4.0)
  - Android 16.0.1 (up from 15.4.0)

## [7.4.0] - 2023-02-13

### Changed:

- Public: Updated underlying Onfido native SDKs versions:
  - iOS 27.4.0 (up from 27.3.0)
  - Android 15.4.0 (up from 15.3.0)

## [7.3.0] - 2023-01-12

### Changed:

- Public: Updated underlying Onfido native SDKs versions:
  - iOS 27.3.0 (up from 27.2.0)
  - Android 15.3.0 (up from 15.1.0)

## [7.2.0] - 2022-12-23

### Added

- Public: Extended localisation support to 44 languages
- Public: Added RTL languages support

### Changed:

- Public: Update underlying Onfido native SDK versions:
  - iOS 27.2.0 (up from 27.0.0)
  - Android 15.1.0 (up from 14.0.0)

### Fixed:

- Public: Remove NFC dependencies from the SDK. They should be added in your app when utilizing NFC.

## [7.1.0] - 2022-12-07

### Added:

- Public: Added NFC support
- Public: Added support for new OnfidoCaptureType - MOTION

## [7.0.1] - 2022-11-28

### Changed:

- Public: Update underlying Onfido native SDK versions:
  - iOS 27.0.0 (up from 26.1.1)
  - Android 14.0.0 (up from 13.0.0)
- Public: Fixes on Typescript issues
- Public: Upgrade min supported version to 0.68.2 (up from 0.60.0)

## [6.1.0] - 2022-11-07

### Changed:

- Public: Update underlying Onfido native SDK versions:
  - iOS 26.1.1 (up from 26.0.1)
- Public: Start supporting Typescript
- Public: Fix iOS build on older Xcode versions

## [6.0.0] - 2022-09-13

### Changed:

- Public: Update underlying Onfido native SDK versions:
  - Android 13.0.0 (up from 12.2.0)
  - iOS 26.0.1 (up from 25.1.0)
- Public: Update supported React version to latest
- Public: Fixed Android build with Java issues

## [5.4.0] - 2022-06-22

### Changed:

- Public:
- Public: Update underlying Onfido native SDK versions:
  - Android 12.2.0 (up from 11.5.0)
  - iOS 25.1.0 (up from 24.6.0)

## [5.3.0] - 2022-05-02

### Changed:

- Public: Update underlying Onfido native SDK versions:
  - Android 11.5.0 (up from 11.4.1)
  - iOS 24.6.0 (up from 24.5.0)

## [5.2.0] - 2022-04-18

### Changed:

- Public: Updated underlying Onfido native SDKs versions:
  - Android 11.4.1 (up from 11.3.0)
  - iOS 24.5.0 (up from 24.3.0)

## [5.1.0] - 2022-03-18

### Changed:

- Public: Upgraded to React v17.0.1 and React Native v0.64.3
- Public: Updating Country Code list to match iOS SDK

## [5.0.0] - 2022-02-18

### Changed:

- Public: Updated underlying Onfido iOS SDK to version 24.3.0 (up from 23.1.0).
  For more information visit the [Onfido iOS SDK releases on Github](https://github.com/onfido/onfido-ios-sdk/releases)
  NOTE: This version of SDK will only support building your app with Xcode 13 due to lack of backward compatibility introduced by Apple with Xcode 13.
- Public: Updated underlying Onfido Android SDK to version 11.3.0 (up from 10.3.2).
  For more information visit the [Onfido Android SDK releases on Github](https://github.com/onfido/onfido-android-sdk/releases)

## [4.0.1] - 2022-01-28

### Changed:

- Public: Fixed compile time issue that happened on Android due to wrong parsing of the author payload from `package.json` file
- Public: Removed the final screen from being always shown at the end of the flow steps on Android

## [4.0.0] - 2022-01-13

### Changed:

- Public: Upgraded the Gradle version of the Onfido's React Native SDK to v7.0.2
- Public: Upgraded the Android Gradle Plugin to v7.0.4

## [3.0.0] - 2021-12-22

### Changed:

- Public: Updated underlying Onfido iOS SDK to version 23.1.0 (up from 22.3.0). For more information visit the [Onfido iOS SDK releases on Github](https://github.com/onfido/onfido-ios-sdk/releases) NOTE: This version of SDK will only support building your app with Xcode 13 due to lack of backward compatibility introduced by Apple with Xcode 13.
- Public: Updated underlying Onfido Android SDK to version 10.3.2 (up from 10.1.0). For more information visit the [Onfido Android SDK releases on Github](https://github.com/onfido/onfido-android-sdk/releases)

## [2.2.1] - 2022-01-25

### Changed:

- Public: Removed the final screen from being always shown at the end of the flow steps on Android

## [2.2.0] - 2021-09-15

### Changed:

- Public: Updated underlying Onfido iOS SDK to version 22.3.0 (up from 21.4.0). For more information visit the [Onfido iOS SDK releases on Github](https://github.com/onfido/onfido-ios-sdk/releases)
- Public: Updated underlying Onfido Android SDK to version 10.1.0 (up from 9.3.1). For more information visit the [Onfido Android SDK releases on Github](https://github.com/onfido/onfido-android-sdk/releases)

### Fixed:

- Public: Fixed script that added Onfido bintray link to users' `android/app/build.gradle` to now add `mavenCentral()` instead, if not already present.
- Public: Fixed sample Localizable file to match newest iOS string keys.

## [2.1.1] - 2021-07-27

### Changed:

- Public: Updated React peer dependency to include up to versions 17.0.x

## [2.1.0] - 2021-07-19

### Changed:

- Public: Updated error messages to propagate more info from underlying native SDKs.

## [2.0.0] - 2021-06-11

### Added:

- Public: Added support for enabling the user consent screen for iOS and Android.

### Changed:

- Public: Updated underlying Onfido iOS SDK to version 21.4.0 (up from 20.1.0). For more information visit the [Onfido iOS SDK releases on Github](https://github.com/onfido/onfido-ios-sdk/releases)
- Public: Updated underlying Onfido Android SDK to version 9.3.1 (up from 9.0.0). For more information visit the [Onfido Android SDK releases on Github](https://github.com/onfido/onfido-android-sdk/releases)

## [1.3.4] - 2021-05-18

### Added:

- Public: Updated README with troubleshooting information and okhttp3 resolution guide.
- Public: Updated error responses to be more descriptive.

### Fixed:

- Public: Apply okhttp3 fix to SampleApp.
- Public: Add fix for XCode 12.5 to SampleApp Podfile.
- Public: Fix an iOS bug where the Onfido flow isn't presented to user if the rootViewController is not at the top of the view hierarchy.
- Public: Fix for bug with Android proguard rules

## [1.3.3] - 2021-02-26

### Added:

- Public: Added support for hide logo and cobranding enterprise features

## [1.3.2] - 2020-09-29

### Fixed:

- UI: Fixed iOS crash problem on Xcode12 simulator

## [1.3.1] - 2020-09-02

### Fixed:

- UI: Fixed iOS custom appearance problem on real device

## [1.3.0] - 2020-08-04

### Changed:

- Public: Upgraded android SDK version to 7.2.0

## [1.2.2] - 2020-07-16

### Fixed:

- Public: Added fix in Github Repository filter

## [1.2.0] - 2020-07-07

### Added:

- Public: Added custom localisation support for ios

### Changed:

- Public: Upgraded iOS SDK version

## [1.0.1] - 2020-04-16

### Fixed:

- Public: Added fix for bug where npm install in SampleApp is deleting files in SampleApp directory.

## [1.0.0] - 2020-04-15

### Added:

- Public: MVP release of React Native SDK.
