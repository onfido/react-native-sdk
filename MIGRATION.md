# Onfido SDK Migration Guides

These guides below are provided to ease the transition of existing applications using the Onfido React Native SDK from one version to another that introduces breaking API changes.

* [Onfido react native SDK 9.0.0 Migration Guide](#onfido-rn-sdk-900-migration-guide)

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

- Infact, `options` key was entirely removed from `captureFace`. `captureFace` can be one of the 3 following types:

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


