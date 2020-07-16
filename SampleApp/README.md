# Sample App for @onfido/react-native-sdk

## Summary

You can use this sample app to test the React Native SDK and get an idea of how to integrate with it.

## Install the required tools

### Install required Android tools

Make sure you have installed:
- node (https://nodejs.org/en/download/)
- watchman (`brew install watchman`)
- [JDK 8 or newer](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Android Studio](https://developer.android.com/studio/index.html), configured as specified in [the react-native guide for Android](https://facebook.github.io/react-native/docs/getting-started.html#android-development-environment)
- Open `AVD Manager` in `Android Studio`, Click `+ Add Device` and install `Marshmallow 23 x86_64` image.

Set up your device:
- To run on a physical device, [enable debugging, and plug it in](https://facebook.github.io/react-native/docs/running-on-device.html#1-enable-debugging-over-usb)
- To run on an emulator, [ensure you have an AVD created](https://facebook.github.io/react-native/docs/getting-started.html#using-a-virtual-device), and running (or follow instructions below on how to use custom commands for managing AVDs)

### Install required iOS tools

Make sure you have installed:
- node (https://nodejs.org/en/download/)
- Xcode 10+ (https://developer.apple.com/download/)
- watchman (`brew install watchman`)
- pods (`gem install cocoapods`)

Set up your device:
- Follow the [official documentation](https://facebook.github.io/react-native/docs/running-on-device) to setup and run app within a physical device

## Step 1: Setup

### Set up the API Token
Before running the sample app you will need to open SampleApp/backend-server-example.js and replace 'YOUR_API_TOKEN_HERE' with your API token. You can use our [sandbox](https://documentation.onfido.com/#sandbox-testing) environment to test the integration, and you will find the API tokens inside your [Onfido Dashboard](https://onfido.com/dashboard/api/tokens). You can create API tokens inside your Onfido Dashboard as well.

| :warning: Do not use your API token in your client code.  This server code is only included here as an example. |
| --- |

### Set up custom colors
You can change the colors in SampleApp/colors.json if you wish to customize the sample app but, this is not required.

After changing the colors, run the following to apply your changes to your Android build:
```shell
cd SampleApp
yarn
npm --prefix node_modules/@onfido/react-native-sdk/ run updateColors
```

## Step 2: Build and run the sample app
All commands should start in the `SampleApp/` directory:
```shell
cd SampleApp
```

In one console, run the following to install the necessary packages, run the build, and start the server.  It may take 2 or more minutes to build.  
```shell
yarn && cd ios && pod install && cd .. && watchman watch-del-all && npx react-native start --reset-cache
```

Open a second console.  Launch the iOS app:
```shell
npx react-native run-ios
```

Launch the Android app:
```shell
npx react-native run-android
```

# Testing the code

## Unit

`yarn test` will kick off the unit tests.

## End-to-end

We have end-to-end tests that run through the app on emulator.

### Android

Create an emulator (naming **"Detox_Emulator"** is important):

    yarn create-avd -n Detox_Emulator

In one terminal window compile the code for end-to-end testing:

    yarn start-e2e-android-sampleapp

In another boot the Android emulator & automated tests:

    yarn test-e2e-android-debug-sampleapp
