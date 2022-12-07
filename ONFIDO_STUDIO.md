## Overview
[Onfido Studio](https://developers.onfido.com/guide/onfido-studio-product) is a drag and drop interface enabling you to build an optimised route to verify each end user, by defining and configuring different paths, as well as incorporating a combination of signals, in a single identity verification flow.

## Integrating with React Native SDK
The Onfido React Native SDK provides a drop-in set of screens and tools for React Native applications to capture identity documents and selfie photos and videos for the purpose of identity verification.

The SDK communicates directly and dynamically with active workflows to show the relevant screens to ensure the correct capture and upload of user information. As a result, the SDK flow will vary depending on the workflow configuration. You won't need to specify any steps directly in the SDK integration as these will be overridden when the workflow run ID is passed into the SDK initialisation.


> ℹ️ 
> 
> The following guide will help you to integrate with Onfido Studio.
> If you are looking for the standard integration using Onfido checks, please head to our [README](https://github.com/onfido/react-native-sdk).

## Getting started 

The SDK supports:

* React Native 0.68+
* iOS 11+
* Supports Xcode 13+
* Supports Android API level 21+


### 1. Add the SDK dependency

#### Using npm
The SDK is available on npm and you can include it in your project by running the following script from your project folder:

```shell
npm install @onfido/react-native-sdk --save​
```

### 2. Update your `Android build.gradle` files
Update your `build.gradle` files to reference the Android SDK, and enable multi-dex. If you build your project using the react-native init, with a `build.gradle` in the `android/` and `android/app/` directories, you can run this script to do it:

```shell
npm --prefix node_modules/@onfido/react-native-sdk/ run updateBuildGradle
```

### 3. Update your iOS configuration files
Change ios/Podfile to use version 11:

```ruby
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

Open Xcode and create an empty Swift file in your project root. For example, if your project is called YourProjectName, you can open it from the command line:

```shell
open ios/YourProjectName.xcodeproj
``` 

Once Xcode is open, add an empty Swift file: 

`File > New File > Swift > Next > "SwiftVersion" > Create > Don't create Header`

This will update your iOS configuration with a Swift version. All changes are automatically saved, so you can close Xcode.


### 4. Build a configuration object

To initiaise the SDK, you must provide a `workflowRunId`, obtained by [creating a workflow run](https://documentation.onfido.com/#create-workflow-run), and an `sdkToken`, obtained by [generating an SDK token](https://documentation.onfido.com/#generate-sdk-token). 

```javascript
const config = {
  sdkToken: '<YOUR_SDK_TOKEN>',
  workflowRunId: '<YOUR_WORKFLOW_RUN_ID>'
};
```    

### 5. Start the flow
```javascript
Onfido.start(config);
// listen for the result
```    

## Handling callbacks

To receive the result from a completed workflow, you should use promises. For example:


```javascript
Onfido.start(config)
      .then(res => console.warn('OnfidoSDK: Success:', JSON.stringify(res)))
      .catch(err => console.warn('OnfidoSDK: Error:', err.code, err.message));
```


| ATTRIBUTE        | NOTES           |
| ------------- |-------------|
| .then    | The end user completed all interactive tasks in the workflow. If you have configured [webhooks](https://documentation.onfido.com/#webhooks), a notification will be sent to your backend confirming the workflow run has finished. You do not need to create a check using your backend as this is handled directly by the Workflow.  |
| .error(Error)      | An unexpected error occurred.      |

### Customizing the SDK

Onfido Studio uses the same appearance and localization objects as a standard integration. You can see how to create them here: [Appearance](https://github.com/onfido/react-native-sdk#theme-customization) and [Localization](https://github.com/onfido/react-native-sdk#5-localization).