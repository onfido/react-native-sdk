![npm](https://img.shields.io/npm/v/@onfido/react-native-sdk?color=%47b801)
![NPM](https://img.shields.io/npm/l/@onfido/react-native-sdk?color=%47b801)
![Build Status](https://app.bitrise.io/app/8e301f076fdc3e94/status.svg?token=7lDTdIn1dfL81q2VwjUpFA&branch=master)

## Table of contents

- [Table of contents](#table-of-contents)
- [Overview](#overview)
- [Getting started](#getting-started)
  - [1. Obtaining an API token](#1-obtaining-an-api-token)
  - [2. Creating an Applicant](#2-creating-an-applicant)
  - [3. Configuring SDK with Tokens](#3-configuring-sdk-with-tokens)
  - [4. Adding the Onfido React Native SDK to your project](#4-adding-the-onfido-react-native-sdk-to-your-project)
    - [This SDK supports React Native versions 0.68.2 and later](#this-sdk-supports-react-native-versions-0682-and-later)
    - [4.1 Adding SDK dependency through npm](#41-adding-sdk-dependency-through-npm)
    - [4.2 Update your Android build.gradle files](#42-update-your-android-buildgradle-files)
    - [4.3  Custom Android Application Class](#43--custom-android-application-class)
      - [Kotlin](#kotlin)
    - [4.4 Update your iOS configuration files](#44-update-your-ios-configuration-files)
      - [Enabling NFC extraction](#enabling-nfc-extraction)
- [Usage](#usage)
  - [User data](#user-data)
  - [1. Creating the SDK configuration](#1-creating-the-sdk-configuration)
  - [2. Parameter details](#2-parameter-details)
  - [3. Success Response](#3-success-response)
  - [4. Failure Response](#4-failure-response)
  - [5. Localization](#5-localization)
    - [Android](#android)
    - [iOS](#ios)
- [Creating checks](#creating-checks)
  - [1. Obtaining an API token](#1-obtaining-an-api-token-1)
  - [2. Creating a check](#2-creating-a-check)
- [Theme Customization](#theme-customization)
  - [Android](#android-1)
  - [iOS](#ios-1)
- [Going live](#going-live)
- [More Information](#more-information)
  - [Troubleshooting](#troubleshooting)
  - [Discrepancies between underlying Onfido native SDKs](#discrepancies-between-underlying-onfido-native-sdks)
  - [Support](#support)
- [How is the Onfido React Native SDK licensed?](#how-is-the-onfido-react-native-sdk-licensed)


## Overview

This SDK provides a drop-in set of screens and tools for react native applications to allow capturing of identity documents and face photos/live videos for the purpose of identity verification with [Onfido](https://onfido.com/). The SDK offers a number of benefits to help you create the best on-boarding/identity verification experience for your customers:

* Carefully designed UI to guide your customers through the entire photo/video-capturing process
* Modular design to help you seamlessly integrate the photo/video-capturing process into your application flow
* Advanced image quality detection technology to ensure the quality of the captured images meets the requirement of the Onfido identity verification process, guaranteeing the best success rate
* Direct image upload to the Onfido service, to simplify integration\*

> ℹ️ 
> 
> If you are integrating using Onfido Studio please see our [Studio integration guide](ONFIDO_STUDIO.md)

\* **Note**: the SDK is only responsible for capturing and uploading photos/videos. You still need to access the [Onfido API](https://documentation.onfido.com/) to create and manage checks.

* Supports iOS 11+
* Supports Xcode 13+
* Supports Android API level 21+
* Supports iPads and tablets

## Getting started

### 1. Obtaining an API token

In order to start integration, you will need the **API token**. You can use our [sandbox](https://documentation.onfido.com/#sandbox-testing) environment to test your integration, and you will find the API tokens inside your [Onfido Dashboard](https://onfido.com/dashboard/api/tokens). You can create API tokens inside your Onfido Dashboard as well.

### 2. Creating an Applicant

You must create an Onfido [applicant](https://documentation.onfido.com/#applicants) before you start the flow.

For a document or face check the minimum applicant details required are `firstName` and `lastName`.

You must create applicants from your server:

```shell
$ curl https://api.onfido.com/v3/applicants \
    -H 'Authorization: Token token=YOUR_API_TOKEN' \
    -d 'first_name=Theresa' \
    -d 'last_name=May'
```

The JSON response has an `id` field containing a UUID that identifies the applicant. All documents or live photos/videos uploaded by that instance of the SDK will be associated with that applicant.

### 3. Configuring SDK with Tokens

You will need to generate and include a short-lived JSON Web Token (JWT) every time you initialise the SDK.

To generate an SDK Token you should perform a request to the SDK Token endpoint in the Onfido API:

```shell
$ curl https://api.onfido.com/v3/sdk_token \
  -H 'Authorization: Token token=YOUR_API_TOKEN' \
  -F 'applicant_id=YOUR_APPLICANT_ID' \
  -F 'application_id=YOUR_APPLICATION_BUNDLE_IDENTIFIER'
```

Make a note of the token value in the response, as you will need it later on when initialising the SDK.

**Warning:** SDK tokens expire 90 minutes after creation.

The `application_id` is the "Application ID" or "Bundle ID" that was already set up during development.
* For iOS this is usually in the form of `com.your-company.app-name`.
  * To get this value manually, open xcode `ios/YourProjectName`, click on the project root, click the General tab, under Targets click your project name, and check the Bundle Identifier field.
  * To get this value programmatically in native iOS code, see [Stack Overflow Page](https://stackoverflow.com/questions/8873203/how-to-get-bundle-id).
* For Android this is usually in the form of com.example.yourapp.
  * To get this file manually, you can find it in your app's `build.config`.  For example, in `android/app/build.gradle`, it is the value of `applicationId`.
  * To get this value programmatically in native Java code, see [Stack Overflow Page](https://stackoverflow.com/questions/14705874/bundle-id-in-android).

### 4. Adding the Onfido React Native SDK to your project

#### This SDK supports React Native versions 0.68.2 and later

If you are starting from scratch, you can follow the React Native CLI Quickstart https://reactnative.dev/docs/getting-started.  For examples, once you have installed the React Native tools, you can run:
```shell
$ npx react-native init YourProjectName
```

You cannot use this SDK with Expo: If your project already uses Expo, you will need to follow the eject process https://docs.expo.io/versions/latest/workflow/customizing/.

- NOTE: You will need to download and install [Android Studio](https://developer.android.com/studio/index.html), configured as specified in [the react-native guide for Android](https://facebook.github.io/react-native/docs/getting-started.html#android-development-environment) to run on an android emulator.

#### 4.1 Adding SDK dependency through npm

Navigate to the root directory of your React Native project. The rest of this section (section 4) will assume you are in the root directory. Run the following command:

```shell
$ npm install @onfido/react-native-sdk --save
```

#### 4.2 Update your Android build.gradle files

Update your build.grade files to reference the Android SDK, and enable multi-dex.  If you build your project using the `react-native init`, with a `build.gradle` in the `android/` and `android/app/` directories, you can run this script to do it:

```shell
$ npm --prefix node_modules/@onfido/react-native-sdk/ run updateBuildGradle
```

<details>
<summary>To manually update build files without the script</summary>

If you want to manually update your build files, you can follow the steps the script takes:

Add the maven link `android/build.gradle`:
```gradle
allprojects {
  repositories {
    mavenCentral()
  }
}
```

Enable multidex in `android/app/build.gradle`:
```gradle
android {
  defaultConfig {
     multiDexEnabled true
  }
}
```
</details>

</br>

##### Enabling NFC extraction

With version 10.0.0 of the Onfido React Native SDK, NFC is enabled by default and offered to customer when both the document and the device support NFC.

For more information on how to configure NFC and the list of supported documents, please refer to the [NFC for Document Report](https://developers.onfido.com/guide/document-report-nfc) guide.


NFC dependencies are not included in the SDK to avoid increasing the SDK size when the NFC feature is disabled. To use the NFC feature, you need to include the following dependencies (with the specified versions) in your build script:

```gradle
implementation "net.sf.scuba:scuba-sc-android:0.0.23"
implementation "org.jmrtd:jmrtd:0.7.34"
implementation "com.madgag.spongycastle:prov:1.58.0.0"
```

You also need to add the following Proguard rules to your `proguard-rules.pro` file:

```
-keep class org.jmrtd.** { *; }
-keep class net.sf.scuba.** {*;}
-keep class org.bouncycastle.** {*;}
-keep class org.spongycastle.** {*;}
-keep class org.ejbca.** {*;}

-dontwarn kotlin.time.jdk8.DurationConversionsJDK8Kt
-dontwarn org.ejbca.**
-dontwarn org.bouncycastle.**
-dontwarn org.spongycastle.**

-dontwarn module-info
-dontwarn org.jmrtd.**
-dontwarn net.sf.scuba.**
```


</br>

#### 4.3  Custom Android Application Class
**Note**: You can skip this step if you don't have any custom application class.

⚠️ After the release of version 9.0.0, Onfido RN SDK runs in a separate process. This means that when the Onfido SDK started, a new application instance will be created. To prevent reinitializing you have in the Android application class, you can use the `isOnfidoProcess` extension function and return from `onCreate` as shown below:

This will prevent initialization-related crashes such as: [`FirebaseApp is not initialized in this process`](https://github.com/firebase/firebase-android-sdk/issues/4693)

##### Kotlin

```kotlin
class YourCustomApplication : MultiDexApplication() {
	override fun onCreate() {
	    super.onCreate()
	    if (isOnfidoProcess()) {
	        return
	    }
	    
	    // Your custom initialization calls ...
	 }

  private fun isOnfidoProcess(): Boolean {
    val pid = Process.myPid()
    val manager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager

    return manager.runningAppProcesses.any {
        it.pid == pid && it.processName.endsWith(":onfido_process")
    }
  }
}

```

#### 4.4 Update your iOS configuration files

Change `ios/Podfile` to use version 11:
```
platform :ios, '11.0'
```

Add descriptions for camera and microphone permissions to `ios/YourProjectName/Info.plist`:
```xml
<plist version="1.0">
<dict>
  <!-- Add these four elements: -->
	<key>NSCameraUsageDescription</key>
	<string>Required for document and facial capture</string>
	<key>NSMicrophoneUsageDescription</key>
	<string>Required for video capture</string>
  <!-- ... -->
</dict>
</plist>
```

Open Xcode and create an empty swift file in your project root.  For example, if your project is called YourProjectName, you can open it from the command line:
```bash
open ios/YourProjectName.xcodeproj
```

Once Xcode is open, add an empty Swift file:  File > New File > Swift > Next > "SwiftVersion" > Create > Don't create Header.  This will update your iOS configuration with a Swift version.  All changes are automatically saved, so you can close Xcode.

Install the pods:
```bash
cd ios
pod install
cd ..
```

##### Enabling NFC extraction

With version 10.0.0 of the Onfido React Native SDK, NFC is enabled by default and offered to customer when both the document and the device support NFC.

For more information on how to configure NFC and the list of supported documents, please refer to the [NFC for Document Report](https://developers.onfido.com/guide/document-report-nfc) guide.

This feature requires Near Field Communication Tag Reading capability in your app target. If you haven't added it before, please follow the steps in Apple's documentation.


You're required to have the following key in your application's Info.plist file:

```xml
<key>NFCReaderUsageDescription</key>
<string>Required to read ePassports</string>
```

You have to include the entries below in your app target's Info.plist file to be able to read NFC tags properly.

```xml
<key>com.apple.developer.nfc.readersession.felica.systemcodes</key>
<array>
  <string>12FC</string>
</array>
<key>com.apple.developer.nfc.readersession.iso7816.select-identifiers</key>
<array>
  <string>A0000002471001</string>
  <string>A0000002472001</string>
  <string>00000000000000</string>
  <string>D2760000850101</string>
</array>
```

## Usage

You can launch the app with a call to `Onfido.start`.  For example, once you have the `sdkTokenFromOnfidoServer`, your react component might look like this:

```javascript
import React, {Component} from 'react';
import {Button, View} from 'react-native';
import {
  Onfido,
  OnfidoCaptureType,
  OnfidoCountryCode,
  OnfidoDocumentType,
} from '@onfido/react-native-sdk';

export default class App extends Component {
  startSDK() {
    Onfido.start({
      sdkToken: 'sdkTokenFromOnfidoServer',
      flowSteps: {
        welcome: true,
        captureFace: {
          type: OnfidoCaptureType.VIDEO,
        },
        captureDocument: {
          docType: OnfidoDocumentType.DRIVING_LICENCE,
          countryCode: OnfidoCountryCode.GBR,
        },
      },
    })
      .then(res => console.warn('OnfidoSDK: Success:', JSON.stringify(res)))
      .catch(err => console.warn('OnfidoSDK: Error:', err.code, err.message));
  }

  render() {
    return (
      <View style={{marginTop: 100}}>
        <Button title="Start Onfido SDK" onPress={() => this.startSDK()} />
      </View>
    );
  }
}

### Media Callbacks (beta)

### Introduction
Onfido provides the possibility to integrate with our Smart Capture SDK, without the requirement of using this data only through the Onfido API. Media callbacks enable you to control the end user data collected by the SDK after the end user has submitted their captured media. As a result, you can leverage Onfido’s advanced on-device technology, including image quality validations, while still being able to handle end users’ data directly. This unlocks additional use cases, including compliance requirements and multi-vendor configurations, that require this additional flexibility.

**This feature must be enabled for your account.** Please contact your Onfido Solution Engineer or Customer Success Manager.

### Implementation
To use this feature, use `Onfido.addCustomMediaCallback` and provide the callback.

```javascript
Onfido.addCustomMediaCallback(
  mediaResult => {
    if (mediaResult.captureType === 'DOCUMENT') {
      // Callback code here
    } else if (mediaResult.captureType === 'FACE') {
      // Callback code here
    } else if (mediaResult.captureType === 'VIDEO') {
      // Callback code here
    }
  }
);
```

### User data
The callbacks return an object including the information that the SDK normally sends directly to Onfido. The callbacks are invoked when the end user confirms submission of their image through the SDK’s user interface.

**Note:** Currently, end user data will still automatically be sent to the Onfido backend, but you are not required to use Onfido to process this data.

The callback returns 3 possible objects. Please note, `captureType` refers to the type of the media capture in each case.
These can be `DOCUMENT`, `FACE` or `VIDEO`.

1. For documents(`captureType` is `DOCUMENT`), the callback returns:
```json5
     {
         captureType: String
         side: String
         type: String
         issuingCountry: String?
         fileData: Data
         fileName: String
         fileType: String
     }
```

**Note:** `issuingCountry` is optional based on end-user selection, and can be `null`.
**Note:** If a document was scanned using NFC, the callback will return the passport photo in `file` but no additional data.

2. For live photos (`captureType` is `FACE`), the callback returns:
```json5
{
    captureType: String
    fileData: Data
    fileName: String
    fileType: String
}
```

3. For videos(`captureType` is `VIDEO`), the callback returns:
```json5
{
    captureType: String
    fileData: Data
    fileName: String
    fileType: String
}
```

### 1. Creating the SDK configuration

Once you have an added the SDK as a dependency and you have a SDK token, you can configure the SDK:

Example configuration:

```javascript
config = {
  sdkToken: “EXAMPLE-TOKEN-123”,
  flowSteps: {
    welcome: true,
    captureDocument: {
      docType: OnfidoDocumentType.DRIVING_LICENSE,
      countryCode: OnfidoCountryCode.USA
    },
    captureFace: {
      type: OnfidoCaptureType.VIDEO
    }
  }
}
```

### 2. Parameter details

* **`sdkToken`**: Required.  This is the JWT sdk token obtained by making a call to the SDK token API.  See section [Configuring SDK with Tokens](#3-configuring-sdk-with-tokens).
* **`flowSteps`**: Required.  This object is used to toggle individual screens on and off and set configurations inside the screens.
* **`welcome`**: Optional.  This toggles the welcome screen on or off.  If omitted, this screen does not appear in the flow.
  * Valid values: `true`, `false`
* **`proofOfAddress`**: Optional. This toggles the proof of address screen on or off. If omitted, this screen does not appear in the flow.
  * Valid values: `true`, `false`
* **`captureDocument`**: Optional. This object contains configuration for the capture document screen. If docType and countryCode are not specified, a screen will appear allowing the user to choose these values.  If omitted, this screen does not appear in the flow.
* **`docType`**: Required if countryCode is specified.
  * Valid values in `OnfidoDocumentType`: `PASSPORT`, `DRIVING_LICENCE`, `NATIONAL_IDENTITY_CARD`, `RESIDENCE_PERMIT`, `RESIDENCE_PERMIT`, `VISA`, `WORK_PERMIT`, `GENERIC`.
    **Note**: `GENERIC` document type doesn't offer an optimised capture experience for a desired document type.
* **`countryCode`**: Required if docType is specified.
  * Valid values in `OnfidoCountryCode`: Any ISO 3166-1 alpha-3 code. For example: `OnfidoCountryCode.USA`.
* **`captureFace`**: Optional.  This object object containing options for capture face screen.  If omitted, this screen does not appear in the flow.
* **`type`**: Required if captureFace is specified.
  * Valid values in `OnfidoCaptureType`: `PHOTO`, `VIDEO`, `MOTION`.
* **`showIntro`**: Optional. This toggles the intro screen in Selfie step or the intro video on the Video step. Default `true`
* **`showConfirmation`**: Optional. This toggles the confirmation screen in Video step (Android only). Default `true`
* **`manualVideoCapture`**: Optional. This enables manual video capture (iOS only). Default `false`
* **`motionCaptureFallback`**: Required if captureFace is specified as MOTION.
  * Valid values in `OnfidoFaceCapture`: `OnfidoFaceSelfieCapture`, `OnfidoFaceVideoCapture`.
    * Valid values in `OnfidoFaceSelfieCapture`: `type`: OnfidoCaptureType.PHOTO
    * Valid values in `OnfidoFaceVideoCapture`: `type`: OnfidoCaptureType.VIDEO

    **Note**: In the scenario that the Motion variant is not supported on the user's device, if you configure the `motionCaptureFallback` appropriately it will allow the user to capture a Selfie or a Video as a fallback.
* **`recordAudio`**: Required if captureFace is specified as MOTION.
  * Valid values: `true`, `false`
* **`localisation`**: Optional. This object contains localisation configuration. See section [Localization](#localization) for the details.
  * Example usage:

  ```javascript
  config = {
    sdkToken: “EXAMPLE-TOKEN-123”,
    localisation: {
      ios_strings_file_name: 'Localizable',
    },
    flowSteps: {
      ...
    },
  }
  ```
* `theme`: The theme in which Onfido SDK is displayed. By default, the user's active device theme will be
  automatically applied to the Onfido SDK. However, you can opt out from dynamic theme switching at run time
  and instead set a theme statically at the build time as shown below. In this case, the flow will always be in displayed
  in the selected theme regardless of the user's device theme. 
  * Valid values in `OnfidoTheme`: `AUTOMATIC`, `LIGHT`, `DARK`. 

### 3. Success Response

The response will include a `face` section if `captureFace` was specified, `document` section if `captureDocument` was specified, or both sections if thery were both requested in the config.

Example:

```javascript
{
 document: {
   front: { id: "123-abc" },
   back: { id: "345-def" }
 },
 face: {
   id: "456-567",
   variant: "VIDEO" // PHOTO or VIDEO
 },
}
```

### 4. Failure Response

The SDK will reject the promise any time the Onfido SDK exits without a success.  This includes cases where:
* the configuration was invalid,
* the mobile user clicked the back button to exit the Onfido SDK.

Example

```javascript
{
  code: "config_error",
  message: "sdkToken is missing"
}
```

### 5. Localization

The SDK supports and maintains the following 44 languages:

- Arabic: ar 🇦🇪
- Armenian: hy 🇦🇲
- Bulgarian: bg 🇧🇬
- Chinese (Simplified): zh_Hans 🇨🇳
- Chinese (Traditional): zh_Hant 🇨🇳
- Croatian: hr 🇭🇷
- Czech: cs 🇨🇿
- Danish: da 🇩🇰
- Dutch: nl 🇳🇱
- English (United Kingdom): en_GB 🇬🇧
- English (United States): en_US 🇺🇸
- Estonian: et 🇪🇪
- Finnish: fi 🇫🇮
- French (Canadian): fr_CA 🇫🇷 🇨🇦
- French: fr 🇫🇷
- German: de 🇩🇪
- Greek: el 🇬🇷
- Hebrew: he 🇮🇱
- Hindi: hi 🇮🇳
- Hungarian: hu 🇭🇺
- Indonesian: id 🇮🇩
- Italian: it 🇮🇹
- Japanese: ja 🇯🇵
- Korean: ko 🇰🇷
- Latvian: lv 🇱🇻
- Lithuanian: lt 🇱🇹
- Malay: ms 🇲🇾
- Norwegian: nb 🇳🇴
- Persian: fa 🇮🇷
- Polish: pl 🇵🇱
- Portuguese (Brazil): pt_BR 🇵🇹 🇧🇷
- Portuguese: pt 🇵🇹
- Romanian: ro 🇷🇴
- Russian: ru 🇷🇺
- Serbian: sr_Latn 🇷🇸
- Slovak: sk 🇸🇰
- Slovenian: sl 🇸🇮
- Spanish (Latin America): es_419 🇪🇸 🇺🇸
- Spanish: es 🇪🇸
- Swedish: sv 🇸🇪
- Thai: th 🇹🇭
- Turkish: tr 🇹🇷
- Ukrainian: uk 🇺🇦
- Vietnamese: vi 🇻🇳

However, you can add your own translations.

#### Android

By default, custom localisation is enabled on Android. There is no configuration needed on React Native SDK to enable it.
You could also provide custom translation for a locale that we don’t currently support, by having an additional XML strings file inside your resources folder for the desired locale. See [Localisation section of Android SDK repo](https://github.com/onfido/onfido-android-sdk#4-localisation) for the details.

#### iOS
You can also provide a custom translation for a locale that Onfido doesn't currently support.
There is a simple configuration needed on the React Native SDK to enable custom localisation.

1. Add this statement to your configuration object.
```
localisation: {
  ios_strings_file_name: '<Your .strings file name in iOS app bundle>',
},
```
2. Navigate to the iOS folder ```cd ios```, and open your XCode workspace.
3. Follow the instructions for [iOS Localisation](https://medium.com/lean-localization/ios-localization-tutorial-938231f9f881) to add a new custom language or override existing translations.
4. You can find the keys that need to be translated in the [iOS SDK repo](https://github.com/onfido/onfido-ios-sdk/blob/master/localization/Localizable_EN.strings).

## Creating checks

As the SDK is only responsible for capturing and uploading photos/videos, you would need to start a check on your backend server using the [Onfido API](https://documentation.onfido.com/).

### 1. Obtaining an API token

All API requests must be made with an API token included in the request headers. You can find your API token (not to be mistaken with the mobile SDK token) inside your [Onfido Dashboard](https://onfido.com/dashboard/api/tokens).

Refer to the [Authentication](https://documentation.onfido.com/#authentication) section in the API documentation for details. For testing, you should be using the sandbox, and not the live, token.

### 2. Creating a check

You will need to create a check by making a request to the [create check endpoint](https://documentation.onfido.com/#create-check), using the applicant id. If you are just verifying a document, you only have to include a [document report](https://documentation.onfido.com/#document-report) as part of the check. On the other hand, if you are verifying a document and a face photo/live video, you will also have to include a [facial similarity report](https://documentation.onfido.com/#facial-similarity-report) with the corresponding values: `facial_similarity_photo` for the photo option and `facial_similarity_video` for the video option.

```shell
$ curl https://api.onfido.com/v3/checks \
    -H 'Authorization: Token token=YOUR_API_TOKEN' \
    -d 'applicant_id=YOUR_APPLICANT_ID' \
    -d 'report_names=[document,facial_similarity_photo]'
```

**Note**: You can also submit the POST request in JSON format.

You will receive a response containing the check id instantly. As document and facial similarity reports do not always return actual [results](https://documentation.onfido.com/#results) straightaway, you need to set up a webhook to get notified when the results are ready.

Finally, as you are testing with the sandbox token, please be aware that the results are pre-determined. You can learn more about sandbox responses [here](https://documentation.onfido.com/#pre-determined-responses).

**Note**: If you're using API v2, please check out [API v2 to v3 migration guide](https://developers.onfido.com/guide/v2-to-v3-migration-guide#checks-in-api-v3) to understand which changes need to be applied before starting to use API v3.

## Theme Customization

### Android

Onfido SDK supports the dark theme. By default, the user's active device theme will be
automatically applied to the Onfido SDK. However, you can opt out from dynamic theme switching at run time
and instead set a theme statically at the build time. In this case, the flow will always be in displayed
in the selected theme regardless of the user's device theme. To set a static theme, use the `theme` parameter in the SDK 
initialization config. The value type should be `OnfidoTheme`. 
Valid values in `OnfidoTheme` are: `AUTOMATIC` (default value), `LIGHT`, `DARK`.

You can customize the SDK's appearance by overriding Onfido's light and dark themes (`OnfidoActivityTheme` and `OnfidoDarkTheme`)
in `app/src/main/res/values/styles.xml` or `app/src/main/res/values/themes.xml` in the `android` directory of your project. 
Make sure to set `OnfidoBaseActivityTheme` as the parent of `OnfidoActivityTheme` and `OnfidoBaseDarkTheme` as the parent of `OnfidoDarkTheme` in your style definition.

You can use the following snippet as an example:

```xml
<!-- Light theme -->
<style name="OnfidoActivityTheme" parent="OnfidoBaseActivityTheme">
    <item name="onfidoColorToolbarBackground">@color/brand_dark_blue</item>
    <item name="onfidoColorActionMain">@color/brand_accent_color</item>
</style>

<!-- Dark theme -->
<style name="OnfidoDarkTheme" parent="OnfidoBaseDarkTheme">
   <item name="onfidoColorToolbarBackground">@color/brand_dark_blue</item>
    <item name="onfidoColorActionMain">@color/brand_accent_color</item>
</style>
```

The following attributes are currently supported:

* `onfidoColorToolbarBackground`: Background color of the `Toolbar` which guides the user through the flow

* `colorPrimaryDark`: Color of the status bar (with system icons) above the `Toolbar`

* `onfidoColorContentToolbarTitle`: Color of the `Toolbar`'s title text

* `onfidoColorContentMain`: Color of primary texts on screen, e.g. titles and regular body texts

* `onfidoColorContentSecondary`: Color of secondary texts on screen, e.g. subtitles

* `onfidoColorContentNegative`: Color of error texts

* `onfidoColorActionMain`: Background color of primary buttons

* `onfidoColorActionMainPressed`: Background color of primary buttons when pressed

* `onfidoColorActionMainDisabled`: Background color of primary buttons when disabled

* `onfidoColorContentOnAction`: Text color of primary buttons

* `onfidoColorContentOnActionDisabled`: Text color of primary buttons when disabled

* `onfidoColorActionSecondary`: Background color of secondary buttons

* `onfidoColorActionSecondaryPressed`: Background color of secondary buttons when pressed

* `onfidoColorActionSecondaryDisabled`: Background color of secondary buttons when disabled

* `onfidoColorContentOnActionSecondary`: Text color of secondary buttons

* `onfidoColorContentOnActionSecondaryDisabled`: Text color of secondary buttons when disabled

* `onfidoColorActionSecondaryBorder`: Border of the secondary buttons

* `onfidoColorActionSecondaryBorderDisabled`: Border of the secondary buttons when disabled

* `onfidoColorProgressTrack`: Track color of progress indicators (background color)

* `onfidoColorProgressIndicator`: Indicator color of progress indicators (foreground color)

* `colorAccent`: Defines alert dialogs' accent color, and text input fields' focused underline, cursor, and floating
  label color

* `onfidoColorWatermark`: Color of the Onfido logo and co-brand logo in the footer of screens

* `onfidoColorDisclaimerBackground`: Background color of disclaimer boxes

* `onfidoColorContentDisclaimer`: Text color of disclaimer boxes

* `onfidoColorIconDisclaimer`: Icon color of disclaimer boxes

* `onfidoColorIconStroke`: Stroke color of icons

* `onfidoColorIconFill`: Fill color of icons

* `onfidoColorIconBackground`: Background color of icons

* `onfidoColorIconAccent`: Background color of accented icons

**Note:**
The usage of `color.json` and the `updateColors` command is now deprecated for Android. Please provide the theme attributes in your `styles.xml` or `themes.xml` as mentioned above. 

#### Customizing Dimensions

To customize supported dimnesions, you can add an Android resource file named `dimens.xml` in the following directory of your project: `android/app/src/main/res/values`. 

Please see the snippet below as an example.

```xml
<resources>
  <dimen name="onfidoButtonCornerRadius">8dp</dimen>
</resources>
```

The following dimensions are currently supported:

* **`onfidoButtonCornerRadius`**: The corner radius of all buttons in the SDK, provided in the `dp` unit


### iOS

You can customize the SDK by adding a `colors.json` file to your xcode project as bundle resource. The file should contain a single json object with the desired keys and values.  For example:

```json
{
  "onfidoPrimaryColor": "#FF0000",
  "backgroundColor": {
    "light": "#FCFCFD",
    "dark": "#000000"
  },
  "onfidoPrimaryButtonTextColor": "#FFFFFF",
  "onfidoPrimaryButtonColorPressed": "#FFA500",
  "interfaceStyle": <"unspecified" | "light" | "dark">,
  "secondaryTitleColor": "#FF0000",
  "secondaryBackgroundPressedColor": "#FF0000",
  "buttonCornerRadius": 20,
  "fontFamilyTitle": "FONT_NAME_FOR_TITLES",
  "fontFamilyBody": "FONT_NAME_FOR_CONTENT",
}
```

The following attributes are currently supported:

* **`onfidoPrimaryColor`**: Background color of views such as capture confirmation buttons, back navigation button, and play and pause buttons in liveness/video capture intro
* **`backgroundColor`**: Background color used for all non-capture views. Can be defined for both light and dark mode
* **`onfidoPrimaryButtonTextColor`**: Text color of labels included in views such as capture confirmation buttons
* **`onfidoPrimaryButtonColorPressed`**: Defines the background color of capture confirmation buttons when pressed
* **`interfaceStyle`**: Defines the supported interface styles ("unspecified" by default, which follows the sytem's interface style)
* **`secondaryTitleColor`**: Secondary button text and border color
* **`secondaryBackgroundPressedColor`**: Secondary button pressed state color
* **`buttonCornerRadius`**: Border corner radius for all buttons (default value is 5.0)
* **`fontFamilyTitle`**: Name of the font used for title texts
* **`fontFamilyBody`**: Name of the font used for body/content texts

You can check out the [iOS SampleApp](https://github.com/onfido/onfido-ios-sdk/tree/master/SampleApp) for example usage. When running on an iOS device, the values will be picked up dynamically at runtime. 

**Dark Mode Customization**
`interfaceStyle` allows you to force light or dark mode via `dark` and `light` respectively, or follow the system's interface style with `unspecified`.

**Note:**
The usage of `onfidoIosSupportDarkMode` in the `color.json` is now deprecated. Please use `interfaceStyle` instead.

## Going live

Once you are happy with your integration and are ready to go live, please contact [client-support@onfido.com](mailto:client-support@onfido.com) to obtain live versions of the API token and the mobile SDK token. You will have to replace the sandbox tokens in your code with the live tokens.

A few things to check before you go live:

* Make sure you have entered correct billing details inside your [Onfido Dashboard](https://onfido.com/dashboard/)

## More Information

### Troubleshooting

**Resolving dependency conflicts**

Here are some helpful resources if you are experiencing dependency conflicts between this React Native SDK and other packages your app uses:
* [Gradle: Dependency Resolution](https://docs.gradle.org/current/userguide/dependency_resolution.html#header)
* [Gradle: Dependency Constraints](https://docs.gradle.org/current/userguide/dependency_constraints.html#dependency-constraints)

**General advice**

If you see issues, you can try removing `node_modules`, build directories, and cache files. A good tool to help with this is [react-native-clean-project](https://github.com/pmadruga/react-native-clean-project)
### Discrepancies between underlying Onfido native SDKs

Below is a list of known differences in expected behavior between the Onfido Android and iOS SDKs this React Native SDK wraps:

* Documents with the type `passport` uploaded through the iOS SDK will have the `side` attribute set to `null`, while those uploaded via Android will have `side` as `front`.

### Support

Please open an issue through [GitHub](https://github.com/onfido/onfido-react-native-sdk/issues). Please be as detailed as you can. Remember **not** to submit your token in the issue. Also check the closed issues to check whether it has been previously raised and answered.

If you have any issues that contain sensitive information please send us an email with the `ISSUE:` at the start of the subject to [react-native-sdk@onfido.com](mailto:react-native-sdk@onfido.com?Subject=ISSUE%3A)

Previous version of the SDK will be supported for a month after a new major version release. Note that when the support period has expired for an SDK version, no bug fixes will be provided, but the SDK will keep functioning (until further notice).

Copyright 2021 Onfido, Ltd. All rights reserved.

## How is the Onfido React Native SDK licensed?

The Onfido React Native SDK is available under the MIT license.
