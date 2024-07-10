# Onfido SDK Migration Guides

The guides below are provided to ease the transition of existing applications using the Onfido React Native SDK from one version to another that introduces breaking API changes.

If your migration involves upgrading across multiple SDK versions, be sure to read each individual guide in order to account for all relevant breaking changes.

* [Onfido react native SDK 12.0.0 Migration Guide](#onfido-rn-sdk-900-migration-guide)
* [Onfido react native SDK 9.0.0 Migration Guide](#onfido-rn-sdk-900-migration-guide)

## Onfido React Native SDK 12.0.0 Migration Guide

### Breaking API Changes

- Motion is now supported on all devices. Motion capture fallback configuration has therefore been removed.
  - If you currently set a `motionCaptureFallback` for `captureFace`, then you should be aware that this configuration is no longer available, so you can safely remove it from your integration code.

Before 12.0.0:

```
config = {
  sdkToken: “EXAMPLE-TOKEN-123”,
  flowSteps: {
    welcome: true,
    ...
    captureFace: {
      type: OnfidoCaptureType.MOTION,
      motionCaptureFallback: {
        type: OnfidoCaptureType.PHOTO
      },
    },
  },
}
```

After 12.0.0:

```
config = {
  sdkToken: “EXAMPLE-TOKEN-123”,
  flowSteps: {
    welcome: true,
    ...
    captureFace: {
      type: OnfidoCaptureType.MOTION
    },
  },
}
```

- The option to set the `bubbleErrorBackgroundColor` as part of the appearance configuration has been removed, as this configuration option is not longer available. If you are currently configuring `bubbleErrorBackgroundColor` as part of your `colors.json` file, you can now safely remove it. 

- The flag `supportDarkMode` on the appearance configuration has been removed. Use `userInterfaceStyle` instead to configure the same behaviour.

Before 12.0.0:

```
{
  "onfidoPrimaryColor": "#FF0000",
  "backgroundColor": {
    "light": "#FCFCFD",
    "dark": "#000000"
  },
  "supportDarkMode": true,
  ...
}
```

After 12.0.0:

```
{
  "onfidoPrimaryColor": "#FF0000",
  "backgroundColor": {
    "light": "#FCFCFD",
    "dark": "#000000"
  },
  "userInterfaceStyle": "dark",
  ...
}
```

## Onfido React Native SDK 9.0.0 Migration Guide

### Breaking API Changes

- Extra customization was introduced to the Capture Face step (for more information, see our [readme file](https://github.com/onfido/react-native-sdk/blob/master/README.md#2-parameter-details)). A breaking change was introduced when defining the fallbacks for a Motion type face capture, you can see from the example below how to update.

Before 9.0.0:

```
config = {
  sdkToken: “EXAMPLE-TOKEN-123”,
  flowSteps: {
    welcome: true,
    ...
    captureFace: {
      type: OnfidoCaptureType.MOTION,
      options: VIDEO_CAPTURE_FALLBACK
    },
  },
}
```

After 9.0.0:

```
config = {
  sdkToken: “EXAMPLE-TOKEN-123”,
  flowSteps: {
    welcome: true,
    ...
    captureFace: {
      type: OnfidoCaptureType.MOTION,
      motionCaptureFallback: {
        type: OnfidoCaptureType.PHOTO
      },
    },
  },
}
```

- In fact, `options` key was entirely removed from `captureFace`. `captureFace` can be one of the 3 following types:

```
type OnfidoFaceSelfieCapture = {
  type: OnfidoCaptureType.PHOTO;
  showIntro?: boolean
};

type OnfidoFaceVideoCapture = {
  type: OnfidoCaptureType.VIDEO;
  showIntro?: boolean;
  showConfirmation?: boolean;
  manualVideoCapture?: boolean;
};

type OnfidoFaceMotionCapture = {
    type: OnfidoCaptureType.MOTION;
    recordAudio?: boolean;
    motionCaptureFallback?: OnfidoFaceSelfieCapture | OnfidoFaceVideoCapture;
};
```

- Near Field Communication (NFC) is now enabled by default and offered to customers when both the document and the device support NFC. To disable NFC, please refer to our [NFC reference guide](https://documentation.onfido.com/guide/document-report-nfc#react-native-1).
