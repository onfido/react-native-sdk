![npm](https://img.shields.io/npm/v/@onfido/react-native-sdk?color=%47b801)
![NPM](https://img.shields.io/npm/l/@onfido/react-native-sdk?color=%47b801)
![Build Status](https://app.bitrise.io/app/8e301f076fdc3e94/status.svg?token=7lDTdIn1dfL81q2VwjUpFA&branch=master)

## Table of contents

- [1. Overview](#overview)
- [2. Adding the SDK dependency](#adding-the-sdk-dependency)
- [3. Initializing the SDK](#initializing-the-sdk)
- [4. Completing a session](#completing-a-session)
- [Advanced flow customization](#advanced-flow-customization)
- [Advanced callbacks](#advanced-callbacks)
- [Custom media callbacks](#custom-media-callbacks)
- [More information](#more-information)
- [Raising support issues](#support)

## Overview

The Onfido Smart Capture SDKs provide a set of screens and functionalities that enable applications to implement user identity verification flows. Each SDK contains:

- Carefully designed UX to guide your customers through the different photo or video capture processes
- Modular design to help you seamlessly integrate the different photo or video capture processes into your application's flow
- Advanced image quality detection technology to ensure the quality of the captured images meets the requirement of the
  Onfido identity verification process, guaranteeing the best success rate
- Direct image upload to the Onfido service, to simplify integration
- A suite of advanced fraud detection signals to protect against malicious users

All Onfido Smart Capture SDKs are orchestrated using [Onfido Studio](https://documentation.onfido.com/getting-started/onfido-studio-product) workflows, with only minor customization differences between the available platforms.

### Environments and testing with the SDK

Two environments exist to support the Onfido SDK integrations:

- 'sandbox' - to be used for testing with sample documents
- 'live' - to be used only with real documents and in production apps

The environment being used is determined by the API token that is used to generate the necessary [SDK token](#sdk-authentication).

### Going Live

Once you are satisfied with your integration and are ready to go live, please contact [Customer Support](mailto:client-support@onfido.com) to obtain a live API token. You will have to replace the sandbox token in your code with the live token.

Check that you have entered correct billing details inside your [Onfido Dashboard](https://onfido.com/dashboard/), before going live.

## Adding the SDK dependency

The React Native SDK supports:

* React Native (up to and including version 0.72)
* iOS 12+
* Xcode 15+
* Android API level 21+
* iPads and tablets

### Adding the Onfido React Native SDK to your project

**This SDK supports React Native versions up to and including 0.72**

If you are starting from scratch, you can follow the [React Native CLI Quickstart](https://reactnative.dev/docs/getting-started). Once you have installed the React Native tools, you can run:

```shell
$ npx react-native init YourProjectName
```

You cannot use this SDK with Expo. If your project already uses Expo, you will need to follow the eject process, documented [here](https://docs.expo.io/versions/latest/workflow/customizing/).

**Note**: You will need to download and install [Android Studio](https://developer.android.com/studio/index.html), configured as specified in [the react-native guide for Android](https://facebook.github.io/react-native/docs/getting-started.html#android-development-environment) to run on an Android emulator.

### Adding SDK dependency through npm

Navigate to the root directory of your React Native project. The rest of this section will assume you are in the root directory. Run the following command:

```shell
$ npm install @onfido/react-native-sdk --save
```

#### Update your Android build.gradle files

Update your `build.grade` files to reference the Android SDK, and enable multi-dex. If you build your project using the `react-native init`, with a `build.gradle` in the `android/` and `android/app/` directories, you can run this script to do it:

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

Enable multi-dex in `android/app/build.gradle`:
```gradle
android {
  defaultConfig {
     multiDexEnabled true
  }
}
```
</details>

</br>

##### Custom Android Application Class

**Note**: You can skip this step if you don't have any custom application class.

After the release of version 9.0.0, the Onfido React Native SDK runs in a separate process (for Android only). This means that when the Onfido SDK starts, a new application instance will be created. To prevent re-executing the initializations you have in the Android application class, you can use the `isOnfidoProcess` extension function and return from `onCreate` as shown below.

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

#### Update your iOS configuration files

Change `ios/Podfile` to use version 13:
```
platform :ios, '13.4'
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

Open Xcode and create an empty Swift file in your project root. For example, if your project is called YourProjectName, you can open it from the command line:
```bash
open ios/YourProjectName.xcodeproj
```

Once Xcode is open, add an empty Swift file:  File > New File > Swift > Next > "SwiftVersion" > Create > Don't create Header.  This will update your iOS configuration with a Swift version. All changes are automatically saved, so you can close Xcode.

Install the pods:
```bash
cd ios
pod install
cd ..
```

#### NFC capture using Onfido Studio

Recent passports, national identity cards and residence permits contain a chip that can be accessed using Near Field Communication (NFC). The Onfido SDKs provide a set of screens and functionalities to extract this information, verify its authenticity and provide the resulting verification as part of a Document report.

From version 10.0.0 onwards, NFC is enabled by default in the Onfido React Native SDK for Android and iOS and offered to customers when both the document and the device support NFC.

For more information on how to configure NFC and the list of supported documents, please refer to the [NFC for Document Report](https://documentation.onfido.com/guide/document-report-nfc) guide.

##### Enabling NFC extraction for iOS

For iOS, the NFC feature requires `Near Field Communication Tag Reading` capability in your app target. If you haven't added it before, please follow the steps in [Apple's documentation](https://help.apple.com/xcode/mac/current/#/dev88ff319e7).

You're required to have the following key in your application's `Info.plist` file:

```xml
<key>NFCReaderUsageDescription</key>
<string>Required to read ePassports</string>
```

You have to include the entries below in your app target's `Info.plist` file to be able to read NFC tags properly:

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

##### Disabling NFC and excluding dependencies

NFC is enabled by default. To disable NFC, include the `nfcOption` parameter with `OnfidoNFCOptions.DISABLED` while configuring the `Onfido.start` function:

```javascript
config = {
  sdkToken: "<YOUR_SDK_TOKEN>",
  workflowRunId: "<YOUR_WORKFLOW_RUN_ID>",
  nfcOption: OnfidoNFCOptions.DISABLED
}
```

For Android, a range of NFC library dependencies are included in the build automatically. In addition to configuring the `nfcOption` parameter, you must remove any libraries from the build process.

Exclude dependencies required for NFC from your build:

```gradle
dependencies {
  implementation 'com.onfido.sdk.capture:onfido-capture-sdk:x.y.z' {
    exclude group: 'net.sf.scuba', module: 'scuba-sc-android'
    exclude group: 'org.jmrtd', module: 'jmrtd'
    exclude group: 'com.madgag.spongycastle', module: 'prov'
  }
}
```

If your application already uses the same libraries that the Onfido SDK needs for the NFC feature, you may encounter some dependency conflicts that will impact and could interfere with the NFC capture in our SDK. In such cases, we propose using the dependency resolution strategy below, by adding the following lines to your `build.gradle` file:

```gradle
implementation ("com.onfido.sdk:onfido-<variant>:19.1.0"){
     exclude group: "org.bouncycastle"
 }
 implementation ("the other library that conflicts with Onfido on BouncyCastle") {
     exclude group: "org.bouncycastle"
 }
 
 implementation "org.bouncycastle:bcprov-jdk15to18:1.69"
 implementation "org.bouncycastle:bcutil-jdk15to18:1.69"
```

#### NFC Options

To configure NFC, include the `nfcOption` parameter with the three options below while configuring the `Onfido.start` function:
* DISABLED: NFC reading will not be asked of end-users
* OPTIONAL (Default): NFC reading will be attempted, if possible
* REQUIRED: NFC reading will be enforced, preventing end-users from completing the flow without a successful reading

## Initializing the SDK

> ⚠️ The following SDK initialization documentation applies to identity verification workflows orchestrated using Onfido Studio.
> For integrations where the verification steps are manually defined and configured, please refer to the [Advanced flow customization](#advanced-flow-customization) section below.

The Reach Native SDK has multiple initialization and customization options that provide flexibility to your integration, while remaining easy to integrate.

### Defining a workflow

Onfido Studio is the platform used to create highly reusable identity verification workflows for use with the Onfido SDKs. For an introduction to working with workflows, please refer to our [Getting Started guide](https://documentation.onfido.com/getting-started/general-introduction), or the Onfido Studio [product guide](https://documentation.onfido.com/getting-started/onfido-studio-product).

SDK sessions are orchestrated by a session-specific `workflow_run_id`, itself derived from a `workflow_id`, the unique identifier of a given workflow.

For details on how to generate a `workflow_run_id`, please refer to the `POST /workflow_runs/` endpoint definition in the Onfido [API reference](https://documentation.onfido.com/api/latest#workflow-runs).

<Callout type="warning">

> **Note** that in the context of the SDK, the `workflow_run_id` property is referred to as `workflowRunId`.

</Callout>

#### Applicant ID reuse

When defining workflows and creating identity verifications, we highly recommend saving the `applicant_id` against a specific user for potential reuse. This helps to keep track of users should you wish to run multiple identity verifications on the same individual, or in scenarios where a user returns to and resumes a verification flow.

### SDK authentication

The SDK is authenticated using SDK tokens. Onfido Studio generates and exposes SDK tokens in the workflow run payload returned by the API when a workflow run is [created](https://documentation.onfido.com/#create-workflow-run).

SDK tokens for Studio can only be used together with the specific workflow run they are generated for, and remain valid for a period of five weeks.

**Note**: You must never use API tokens in the frontend of your application as malicious users could discover them in your source code. You should only use them on your server.

### Build a configuration object

To use the SDK, you need to obtain an instance of the client object, using your generated SDK token and workflow run ID.

```javascript
config = {
  sdkToken: "<YOUR_SDK_TOKEN>",
  workflowRunId: "<YOUR_WORKFLOW_RUN_ID>"
}
```

### Start the flow

You can then launch the app with a call to `Onfido.start`.

```javascript
Onfido.start(config);
// listen for the result
```

An expanded example of a configuration can be found below:

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
      sdkToken: "<YOUR_SDK_TOKEN>",
      workflowRunId: "<YOUR_WORKFLOW_RUN_ID>",
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

### Styling customization

For both iOS and Android, the React Native SDK supports the customization of colors, fonts and strings used in the SDK flow.

### Appearance and Colors

#### Android

The customization of colors and other appearance attributes for Android is implemented according to the same methodology as the native Android SDK. You can find detailed documentation [here](https://documentation.onfido.com/sdk/android/#ui-customization).

For a complete list and visualizations of the customizable attributes, refer to our [SDK customization guide](https://documentation.onfido.com/sdk/sdk-customization#ui-customization).

##### Customizing Dimensions

To customize supported dimensions, you can add an Android resource file called `dimens.xml` in the following directory of your project: `android/app/src/main/res/values`. 

For example:

```xml
<resources>
  <dimen name="onfidoButtonCornerRadius">8dp</dimen>
</resources>
```

The following dimension is currently supported:

* **`onfidoButtonCornerRadius`**: The corner radius of all buttons in the SDK, provided in the `dp` unit

#### iOS

For iOS, you can customize colors and other appearance attributes by adding a `colors.json` file to your Xcode project as a bundle resource. The file should contain a single json object with the desired keys and values. For example:

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

For a complete list and visualizations of the customizable attributes, refer to our [SDK customization guide](https://documentation.onfido.com/sdk/sdk-customization#ui-customization).

### Dark theme

The React Native SDK supports the dark theme. By default, the user's active device theme will be automatically applied to the Onfido SDK. However, you can opt out from dynamic theme switching at run time and instead set a theme statically at the build time as shown below. In this case, the flow will always be displayed in the selected theme regardless of the user's device theme.

#### Android

Dark theme customization for Android is implemented according to the same methodology as the native Android SDK. You can find detailed documentation [here](https://documentation.onfido.com/sdk/android/#dark-theme).

#### iOS

Dark theme customization for iOS is implemented according to the same methodology as the native Android SDK. You can find detailed documentation [here](https://documentation.onfido.com/sdk/ios/#dark-theme).

### Co-branding

The Onfido React Native SDK allows for a number of co-branding options that affect the display of the Onfido logo at the bottom of the Onfido screens.

#### Logo co-branding

- **`logoCobrand {Boolean}` - optional**

  You may specify a set of images to be defined in the `logoCobrand` property. You must provide the path to an image for use in 'dark' mode and a separate image for 'light' mode.

##### Android

The two logo images must be placed in the `drawables` resource folder of your application (`/res/drawables`), named as follows:

- `cobrand_logo_light.xml` (accessible in code by `R.drawable.cobrand_logo_light`)
- `cobrand_logo_dark.xml` (accessible in code by `R.drawable.cobrand_logo_dark`)

##### iOS

The two logo images must be placed in the `assets` folder of your application.

The `logoCobrand` property itself takes a boolean as a value, which by default is set to `false`.

#### Hide Onfido logo

- **`hideLogo {Boolean}` - optional**

  In addition to `logoCobrand`, you can also choose to hide the Onfido logo from the footer watermark. The `hideLogo` property takes a boolean as a value, which by default is set to `false`.

To apply the `logoCobrand` and `hideLogo` options for both Android and iOS, define their values in the `Onfido.start` function:

```javascript
    Onfido.start({
      sdkToken: "<TOKEN_HERE>",
      workflowRunId: "<YOUR_WORKFLOW_RUN_ID>",
      hideLogo: "<TRUE/FALSE>",
      logoCobrand: "<TRUE/FALSE>"
    })
```

**Please note**: Logo co-branding options must be enabled by Onfido. Please [contact](mailto:client-support@onfido.com) your Solutions Engineer or Customer Success Manager to activate the feature.

### Language localization

The React Native SDK supports and maintains translations for over 40 languages, available for use with both Android and iOS.

The SDK will detect and use the end user's device language setting. If the device's language is not supported by Onfido, the SDK will default to English (`en_US`).

For a complete list of the languages Onfido supports, refer to our [SDK customization guide](https://documentation.onfido.com/sdk/sdk-customization#language-customization).

#### Custom languages for Android

You can also provide a custom translation for a specific language or locale that Onfido does not currently support, by having an additional XML strings file inside your resources folder for the desired locale. See our [Android localization documentation](https://documentation.onfido.com/sdk/android/#language-localization) for more details.

#### Custom languages for iOS

For iOS, you can also provide a custom translation for a specific language or locale that Onfido does not currently support. To configure this on the React Native SDK:

1. Add this statement to your [configuration object](#build-a-configuration-object).

```
localisation: {
  ios_strings_file_name: '<Your .strings file name in iOS app bundle>',
},
```

2. Navigate to the iOS folder ```cd ios```, and open your Xcode workspace.
3. Follow the instructions for [iOS Localization](https://medium.com/lean-localization/ios-localization-tutorial-938231f9f881) to add a new custom language or override existing translations.
4. You can find the keys that need to be translated in the [iOS SDK repo](https://github.com/onfido/onfido-ios-sdk/blob/master/localization/Localizable_EN.strings).

## Completing a session

### Handling callbacks

When the Onfido SDK session concludes, a range of completion callback functions may be triggered.

For detailed information regarding handling callbacks for identity verification workflows orchestrated using Onfido Studio, please refer to our native [iOS](https://documentation.onfido.com/sdk/ios/#handling-callbacks) and [Android](https://documentation.onfido.com/sdk/android/#handling-callbacks) documentation.

For callbacks for manually-defined verification flows implemented without Onfido Studio, please refer to the [section below](#advanced-callbacks). 

For documentation regarding advanced callbacks used for returning media uploaded by the end user (such as identity documents or face captures), please refer to the [Custom Media Callbacks](#custom-media-callbacks) section of this document.

### Generating verification reports

While the SDK is responsible for capturing and uploading the user's media and data, identity verification reports themselves are generated based on workflows created using [Onfido Studio](https://documentation.onfido.com/getting-started/onfido-studio-product).

For a step-by-step walkthrough of creating an identity verification using Onfido Studio and our SDKs, please refer to our [Quick Start Guide](https://documentation.onfido.com/getting-started/quick-start-guide).

If your application initializes the Onfido React Native SDK using the options defined in the [Advanced customization](#advanced-flow-customization) section of this document, you may [create checks](https://documentation.onfido.com/api/latest#create-check) and [retrieve report results](https://documentation.onfido.com/api/latest#retrieve-report) manually using the Onfido API.
You may also configure [webhooks](https://documentation.onfido.com/api/latest#webhooks) to be notified asynchronously when the report results have been generated.

## Advanced flow customization

This section on 'Advanced customization' refers to the process of initializing the Onfido React Native SDK without the use of Onfido Studio. This process requires a manual definition of the verification steps and their configuration.

The flow step parameters described below are mutually exclusive with `workflowRunId`, requiring an alternative method of instantiating the client and starting the flow.

**Note** that this initialization process is **not recommended** as the majority of new features are exclusively released for Studio workflows.

### Manual SDK authentication

The SDK is authenticated using SDK tokens. As each SDK token must be specific to a given applicant and session, and a new token must be generated each time you initialize the Onfido React Native SDK.

| Parameter | Notes |
|------|------|
| `applicant_id` | **required** <br /> Specifies the applicant for the SDK instance. |
| `application_id` | **required** <br /> The application ID (for iOS "application bundle ID") that was set up during development. For iOS, this is usually in the form `com.your-company.app-name`, or `com.example.yourapp` for Android. Make sure to use a valid `application_id` or you'll receive a 401 error. |

* For iOS, the `application_id` is usually in the form of `com.your-company.app-name`.
  * To get this value manually, open Xcode `ios/YourProjectName`, click on the project root, click the General tab, under Targets click your project name, and check the Bundle Identifier field.
  * To get this value programmatically in native iOS code, refer to this [Stack Overflow Page](https://stackoverflow.com/questions/8873203/how-to-get-bundle-id).
* For Android, the `application_id` is usually in the form of `com.example.yourapp`.
  * To get this file manually, you can find it in your app's `build.config`. For example, in `android/app/build.gradle`, it is the value of `applicationId`.
  * To get this value programmatically in native Java code, refer to this [Stack Overflow Page](https://stackoverflow.com/questions/14705874/bundle-id-in-android).

It's important to note that manually generated SDK tokens in React Native expire after **90 minutes** and cannot be renewed. SDK tokens generated in Onfido Studio when creating workflow runs are **not** affected by this limit.

For details on how to manually generate SDK tokens, please refer to the `POST /sdk_token/` endpoint definition in the Onfido [API reference](https://documentation.onfido.com/#generate-sdk-token).

**Note**: You must never use API tokens in the frontend of your application as malicious users could discover them in your source code. You should only use them on your server.

### Manually building the configuration object

You can launch the app with a call to `Onfido.start`, manually defining the verification steps and configurations required for your flow:

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
      sdkToken: "<YOUR_SDK_TOKEN>",
      flowSteps: {
        welcome: true,
        captureFace: {
          type: OnfidoCaptureType.VIDEO,
        },
        captureDocument: {
          docType: OnfidoDocumentType.DRIVING_LICENCE,
          countryCode: OnfidoCountryCode.GBR
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

#### Parameter details

* **`sdkToken`**: **Required**. This is the SDK token obtained by making a call to the SDK token API, as [documented above](#sdk-authentication).

* **`flowSteps`**: **Required**. This object is used to toggle on or off the individual screens a user will see during the verification flow, and to set configurations for each screen.
  * **`welcome`**: **Optional**. This toggles the welcome screen on or off. If omitted, this screen does not appear in the flow. Valid values are `true` or `false`
  * **`proofOfAddress`**: **Optional**. This toggles the proof of address screen on or off. If omitted, this screen does not appear in the flow. Valid values are `true` or `false`
  * **`captureDocument`**: **Optional**. This object contains configurations for the document capture screen. If `docType` and `countryCode` are not specified, a screen will appear allowing the user to choose the document type and issuing country. If all parameters are not specified or the step is omitted, this screen will not appear in the flow.
    * **`docType`**: **Required** if `countryCode` is specified.
      * Valid values in `OnfidoDocumentType`: `PASSPORT`, `DRIVING_LICENCE`, `NATIONAL_IDENTITY_CARD`, `RESIDENCE_PERMIT`, `RESIDENCE_PERMIT`, `VISA`, `WORK_PERMIT`. <br>
    * **`countryCode`**: **Required** if `docType` is specified.
      * Valid values in `OnfidoCountryCode`: Any ISO 3166-1 alpha-3 code. For example: `OnfidoCountryCode.USA`.
    * **`allowedDocumentTypes`**: **Optional**. If specified, `docType` and `countryCode` must not be specified. This parameter allows you to specify a list of document types that can be selected for all available issuing countries.
      * Valid values in `OnfidoDocumentType`: `PASSPORT`, `DRIVING_LICENCE`, `NATIONAL_IDENTITY_CARD`, `RESIDENCE_PERMIT`, `RESIDENCE_PERMIT`, `VISA`, `WORK_PERMIT`.
  * **`captureFace`**: **Optional**. This object contains configuration options for the face capture screen. If omitted, this screen does not appear in the flow.
    * **`type`**: **Required** if `captureFace` is specified.
      * Valid values in `OnfidoCaptureType`: `PHOTO`, `VIDEO`, `MOTION`.

* **`showIntro`**: **Optional**. A boolean parameter that toggles on or off the intro screen in the Selfie step, or whether to show a preview of the captured video for user confirmation in the Video step. The default value is `true`.

* **`showConfirmation`**: **Optional**. A boolean parameter that toggles on or off the confirmation screen in the Video step (Android only). The default value is `true`.

* **`manualVideoCapture`**: **Optional**. A boolean parameter that enables manual video capture (iOS only). The default value is `false`.

* **`recordAudio`**: **Required** if the `captureFace` type is specified as `MOTION`. Valid values are `true` or `false`.

* **`localisation`**: **Optional**. This object contains custom localization configurations. See the [Localization](#language-localization) section above for more details.
  * Example usage:

  ```javascript
  config = {
    sdkToken: "<YOUR_SDK_TOKEN>",
    localisation: {
      ios_strings_file_name: 'Localizable',
    },
    flowSteps: {
      ...
    },
  }
  ```
* **`theme`**: Parameter to configure dark theme customization ([documented above](#dark-theme)). Valid values in `OnfidoTheme`: `AUTOMATIC`, `LIGHT`, `DARK`. 

## Advanced callbacks

### Handling callbacks

When the Onfido SDK session concludes, a range of completion callback functions may be triggered. The callbacks detailed in this section apply to manually-defined identity verification flows implemented without Onfido Studio. For callbacks for verification workflows orchestrated using Onfido Studio, please refer to the [section above](#completing-a-session).

### Success Response

If the start function is successful, a json file response will include a `face` section if `captureFace` was specified, a `document` section if `captureDocument` was specified, or both sections if they were both requested in the config.

For example:

```javascript
{
 document: {
   front: { id: "123-abc" },
   back: { id: "345-def" },
   nfcMediaId: { id: "789-def" }
 },
 face: {
   id: "456-567",
   variant: "VIDEO" // PHOTO or VIDEO
 },
 proofOfAddress:{
    front: {
      id: "9763"
    },
    back: {
      id: "8263"
    },
    type: {
      id: "6329"
    }
}
```

### Failure Response

The SDK will reject the promise any time the Onfido SDK exits without a success. This includes cases where:

* the configuration was invalid
* the mobile user clicked the back button to exit the Onfido SDK

A json file failure response will include an error code and an error message.

For example:

```javascript
{
  code: "config_error",
  message: "sdkToken is missing"
}
```

### Custom biometric token storage

When using the authentication with local storage solution, by default the SDK manages biometric token storage. The SDK also allows the clients to take control of the token lifecycle and exposes an API to override the default implementation to read and write the token, so it can be stored on device, in cloud, in a keystore or on your premises.

#### Implementation
1. Provide a custom callback using `Onfido.addBiometricTokenCallback`
   Please note that biometric token callback uses `customerUserHash` parameter. This is a unique identifier for the user that can be used as a key for token storage. Feel free to ignore it if you have your own identifier.

```javascript
Onfido.addBiometricTokenCallback({
  onTokenGenerated: (customerUserHash, biometricToken) => {
    // Called when new biometric token is generated during onboarding
    // Use this callback to securely store the biometric token
  },
  onTokenRequested: (customerUserHash, provideToken) => {
    // Called when biometric token is requested during re-authentication
    // Provide the token to the SDK via provideToken("your-biometric-token");
    provideToken(<biometricToken>);
  }
});
```

## Custom Media Callbacks

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

The callbacks return an object including the information that the SDK normally sends directly to Onfido. The callbacks are invoked when the end user confirms submission of their image through the SDK’s user interface.

**Note:** Currently, end user data will still automatically be sent to the Onfido backend, but you are not required to use Onfido to process this data.

The callback returns 3 possible objects. Please note that `captureType` refers to the type of the media capture in each case.
These can be `DOCUMENT`, `FACE` or `VIDEO`.

1. For documents (`captureType` is `DOCUMENT`), the callback returns:
```json5
{
    captureType: String
    side: String
    type: String
    issuingCountry: String?
    fileData: String
    fileName: String
    fileType: String
}
```

**Notes:**
- `issuingCountry` is optional based on end-user selection, and can be `null`.
- `fileData` is a String representation of the byte array data corresponding to the captured photo of the document.
- If a document was scanned using NFC, the callback will return the passport photo in `fileData` but no additional data.

2. For live photos (`captureType` is `FACE`), the callback returns:
```json5
{
    captureType: String
    fileData: String
    fileName: String
    fileType: String
}
```
**Note:** `fileData` is a String representation of the byte array data corresponding to the captured live photo.

3. For live videos (`captureType` is `VIDEO`), the callback returns:
```json5
{
    captureType: String
    fileData: String
    fileName: String
    fileType: String
}
```
**Note:** `fileData` is a String representation of the byte array data corresponding to the captured video.

Please note that, for your convenience, Onfido provides the `byteArrayStringToBase64` helper function to convert the `fileData` from String to a Base64 format. Here is an example of how to use it:
```javascript
let byteArrayString = mediaResult.fileData;
let base64FileData = Onfido.byteArrayStringToBase64(byteArrayString);
```

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

## Support

Should you encounter any technical issues during integration, please contact Onfido's Customer Support team via [email](mailto:support@onfido.com), including the word ISSUE: at the start of the subject line. 

Alternatively, you can search the support documentation available via the customer experience portal, [public.support.onfido.com](http://public.support.onfido.com).

We recommend you update your SDK to the latest version release as frequently as possible. Customers on newer versions of the Onfido SDK consistently see better performance across user onboarding and fraud mitigation, so we strongly advise keeping your SDK integration up-to-date.

You can review our full SDK versioning policy [here](https://documentation.onfido.com/sdk/sdk-version-releases).

## How is the Onfido React Native SDK licensed?

The Onfido React Native SDK is available under the [MIT license](https://opensource.org/license/mit/).

Copyright 2025 Onfido, Ltd. All rights reserved.

