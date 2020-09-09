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
    - [This SDK supports React Native versions 0.60.0 and later](#this-sdk-supports-react-native-versions-0600-and-later)
    - [4.1 Adding SDK dependency through npm](#41-adding-sdk-dependency-through-npm)
    - [4.2 Update your Android build.gradle files](#42-update-your-android-buildgradle-files)
    - [4.3 Update your iOS configuration files](#43-update-your-ios-configuration-files)
- [Usage](#usage)
  - [1. Creating the SDK configuration](#1-creating-the-sdk-configuration)
  - [2. Parameter details](#2-parameter-details)
  - [3. Success Response](#3-success-response)
  - [4. Failure Response](#4-failure-response)
  - [5. Localization](#5-localization)
- [Creating checks](#creating-checks)
  - [1. Obtaining an API token](#1-obtaining-an-api-token-1)
  - [2. Creating a check](#2-creating-a-check)
- [Theme Customization](#theme-customization)
- [Going live](#going-live)
- [More Information](#more-information)
  - [Support](#support)
- [How is the Onfido React Native SDK licensed?](#how-is-the-onfido-react-native-sdk-licensed)


## Overview

This SDK provides a drop-in set of screens and tools for react native applications to allow capturing of identity documents and face photos/live videos for the purpose of identity verification with [Onfido](https://onfido.com/). The SDK offers a number of benefits to help you create the best on-boarding/identity verification experience for your customers:

* Carefully designed UI to guide your customers through the entire photo/video-capturing process
* Modular design to help you seamlessly integrate the photo/video-capturing process into your application flow
* Advanced image quality detection technology to ensure the quality of the captured images meets the requirement of the Onfido identity verification process, guaranteeing the best success rate
* Direct image upload to the Onfido service, to simplify integration\*

\* **Note**: the SDK is only responsible for capturing and uploading photos/videos. You still need to access the [Onfido API](https://documentation.onfido.com/) to create and manage checks.

* Supports iOS 10+
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

#### This SDK supports React Native versions 0.60.0 and later

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
    maven { url "https://dl.bintray.com/onfido/maven" }
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

#### 4.3 Update your iOS configuration files

Change `ios/Podfile` to use version 10:
```
platform :ios, '10.0'
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

Once Xcode is open, add an empty Swift file:  File > New File > Swift > Next > "SwiftVersion" > Create > Don't create Header.  This will update your iOS configuration with a Swift version.  All chaganges are automatically saved, so you can close Xcode.

Install the pods:
```bash
cd ios
pod install
cd ..
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
    },
  },
}
```

### 2. Parameter details

* **`sdkToken`**: Required.  This is the JWT sdk token obtained by making a call to the SDK token API.  See section [Configuring SDK with Tokens](#3-configuring-sdk-with-tokens).
* **`flowSteps`**: Required.  This object is used to toggle individual screens on and off and set configurations inside the screens.
* **`welcome`**: Optional.  This toggles the welcome screen on or off.  If omitted, this screen does not appear in the flow.
  * Valid values: `true`, `false`
* **`captureDocument`**: Optional. This object contains configuration for the capture document screen. If docType and countryCode are not specified, a screen will appear allowing the user to choose these values.  If omitted, this screen does not appear in the flow.
* **`docType`**: Required if countryCode is specified.  
  * Valid values in `OnfidoDocumentType`: `PASSPORT`, `DRIVING_LICENCE`, `NATIONAL_IDENTITY_CARD`, `RESIDENCE_PERMIT`, `RESIDENCE_PERMIT`, `VISA`, `WORK_PERMIT`, `GENERIC`.
    **Note**: `GENERIC` document type doesn't offer an optimised capture experience for a desired document type.
* **`countryCode`**: Required if docType is specified.
  * Valid values in `OnfidoCountryCode`: Any ISO 3166-1 alpha-3 code. For example: `OnfidoCountryCode.USA`.
* **`captureFace`**: Optional.  This object object containing options for capture face screen.  If omitted, this screen does not appear in the flow.
* **`type`**: Required if captureFace is specified.
  * Valid values in `OnfidoCaptureType`: `PHOTO`, `VIDEO`.
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

Onfido SDKs support a number of out-of-the-box translations. However, you can add your own translations.

#### Android

By default, custom localisation is enabled on Android. There is no configuration needed on React Native SDK to enable it.
You could also provide custom translation for a locale that we don’t currently support, by having an additional XML strings file inside your resources folder for the desired locale. See [Localisation section of Android SDK repo](https://github.com/onfido/onfido-android-sdk#4-localisation) for the details.

#### iOS

If you want to enable custom localisation, add this statement to your configuration object.

```
localisation: {
  ios_strings_file_name: '<Your .strings file name in iOS app bundle>',
},
```

In this case SDK will show strings from the provided file. For more detailed explanation please check out [Localisation section of iOS SDK repo.](https://github.com/onfido/onfido-ios-sdk#language-customisation)

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

You can customize the SDK by adding a `colors.json` file to your project at the same level as your `node_modules` directory. The file should contain a single json object with the desired keys and values.  For example:

```json
{
  "onfidoPrimaryColor": "#FF0000",
  "onfidoPrimaryButtonTextColor": "#008000",
  "onfidoPrimaryButtonColorPressed": "#FFA500",
  "onfidoAndroidStatusBarColor": "#A52A2A",
  "onfidoAndroidToolBarColor": "#800080",
  "onfidoIosSupportDarkMode": true
}
```

Below is a description of all available keys:
* **`onfidoPrimaryColor`**: Defines the background color of views such as the document type icon, capture confirmation buttons, and back navigation button.
* **`onfidoPrimaryButtonTextColor`**: Defines the text color of labels included in views such as capture confirmation buttons.
* **`onfidoPrimaryButtonColorPressed`**: Defines the background color of capture confirmation buttons when pressed.
* **`onfidoAndroidStatusBarColor`**: Android only.  Defines the background color of the `Toolbar` that guides the user through the flow.
* **`onfidoAndroidToolBarColor`**: Android only.  Defines the color of the status bar above the `Toolbar`.
* **`onfidoIosSupportDarkMode`**: iOS Only.  Defines if Dark Mode will be supported on SDK screens. The value is true by default.

Once you've added the colors.json to your project, you should add colors.json file to your xcode project as bundle resource. You can create symbolic link (rather than copy paste) to prevent redundancy. You can check out SampleApp project to see example usage.
 Then when running on an iOS device the values will be picked up dynamically at runtime. For Android devices to pick up the values you will need to run the following command at the same level of your `node_modules` directory.  This will also be run when running the `npm --prefix node_modules/@onfido/react-native-sdk/ run updateOnfido` command.

```shell
$ npm --prefix node_modules/@onfido/react-native-sdk/ run updateColors
```

## Going live

Once you are happy with your integration and are ready to go live, please contact [client-support@onfido.com](mailto:client-support@onfido.com) to obtain live versions of the API token and the mobile SDK token. You will have to replace the sandbox tokens in your code with the live tokens.

A few things to check before you go live:

* Make sure you have entered correct billing details inside your [Onfido Dashboard](https://onfido.com/dashboard/)

## More Information

### Support

Please open an issue through [GitHub](https://github.com/onfido/onfido-react-native-sdk/issues). Please be as detailed as you can. Remember **not** to submit your token in the issue. Also check the closed issues to check whether it has been previously raised and answered.

If you have any issues that contain sensitive information please send us an email with the `ISSUE:` at the start of the subject to [react-native-sdk@onfido.com](mailto:react-native-sdk@onfido.com?Subject=ISSUE%3A)

Previous version of the SDK will be supported for a month after a new major version release. Note that when the support period has expired for an SDK version, no bug fixes will be provided, but the SDK will keep functioning (until further notice).

Copyright 2020 Onfido, Ltd. All rights reserved.

## How is the Onfido React Native SDK licensed?

The Onfido React Native SDK is available under the MIT license.
