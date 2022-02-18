# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## next-version

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

##  [2.1.1] - 2021-07-27

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
