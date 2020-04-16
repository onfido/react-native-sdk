package com.onfido.reactnative.sdk;

import android.app.Activity;

import com.facebook.react.bridge.NoSuchKeyException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UnexpectedNativeTypeException;

import java.util.List;
import java.util.ArrayList;

import com.facebook.react.bridge.Arguments;

import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureStep;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureVariant;
import com.onfido.android.sdk.capture.DocumentType;
import com.onfido.android.sdk.capture.utils.CountryCode;
import com.onfido.android.sdk.capture.ui.options.CaptureScreenStep;

public class OnfidoSdkModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    /* package */ final Onfido client;
    private Promise currentPromise = null;
    private final OnfidoSdkActivityEventListener activityEventListener;

    public OnfidoSdkModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.client = OnfidoFactory.create(reactContext).getClient();
        this.activityEventListener = new OnfidoSdkActivityEventListener(client);
        reactContext.addActivityEventListener(activityEventListener);
    }

    private void setPromise(Promise promise) {
        if (currentPromise != null) {
            // If the current promise is not resolved, reject it with a cancellation error.
            // If the current promise is resolved, calling reject will have no effect.
            // Reference https://github.com/facebook/react-native/blob/master/ReactAndroid/src/main/java/com/facebook/react/bridge/PromiseImpl.java#L232-L233
            currentPromise.reject("error", new Exception("New activity was started before old promise was resolved."));
        }
        currentPromise = promise;
        activityEventListener.setCurrentPromise(promise);
    }

    @Override
    public String getName() {
        return "OnfidoSdk";
    }

    /** NOTE: This indirection is used to allow unit tests to mock this method */
    protected Activity getCurrentActivityInParentClass() {
        return super.getCurrentActivity();
    }

    @ReactMethod
    public void start(final ReadableMap config, final Promise promise) {

        setPromise(promise);

        try {
            final String sdkToken;
            final FlowStep[] flowStepsWithOptions;
            try {
                sdkToken = getSdkTokenFromConfig(config);
                flowStepsWithOptions = getFlowStepsFromConfig(config);
            } catch (Exception e) {
                currentPromise.reject("config_error", e);
                currentPromise = null;
                return;
            }
    
            Activity currentActivity = getCurrentActivityInParentClass();
            if (currentActivity == null) {
                currentPromise.reject("error", new Exception("Android activity does not exist"));
                currentPromise = null;
                return;
            }
    
            try {
                final OnfidoConfig onfidoConfig = OnfidoConfig.builder(currentActivity)
                        .withSDKToken(sdkToken)
                        .withCustomFlow(flowStepsWithOptions)
                        .build();
                client.startActivityForResult(currentActivity, 1, onfidoConfig);
            }
            catch (final Exception e) {
                currentPromise.reject("error", new Exception("Failed to show Onfido page", e));
                currentPromise = null;
                return;
            }

        } catch (final Exception e) {
            e.printStackTrace();
            // Wrap all unexpected exceptions.
            currentPromise.reject("error", new Exception("Unexpected error starting Onfido page", e));
            currentPromise = null;
            return;
        }
    }

    public static String getSdkTokenFromConfig(final ReadableMap config) {
        final String sdkToken = config.getString("sdkToken");
        return sdkToken;
    }

    public static FlowStep[] getFlowStepsFromConfig(final ReadableMap config) throws Exception {    
        try {

            final ReadableMap flowSteps = config.getMap("flowSteps");

            final Boolean welcomePageIsIncluded;
            if (flowSteps.hasKey("welcome")) {
                welcomePageIsIncluded = flowSteps.getBoolean("welcome");
            } else {
                welcomePageIsIncluded = false;
            }
    
            ReadableMap captureDocument = null;
            Boolean captureDocumentBoolean = null;
            
            // ReadableMap does not have a way to get multi-typed values without throwing exceptions.
            try {
                captureDocumentBoolean = flowSteps.getBoolean("captureDocument");
            } catch (final NoSuchKeyException | UnexpectedNativeTypeException notFoundException) {
                try {
                    captureDocument = flowSteps.getMap("captureDocument");
                } catch (NoSuchKeyException nske) {
                    captureDocument = null;
                }
            }
    

            final List<FlowStep> flowStepList = new ArrayList<>();

            if (welcomePageIsIncluded) {
                flowStepList.add(FlowStep.WELCOME);
            }

            if (captureDocumentBoolean != null && captureDocumentBoolean) {
                flowStepList.add(FlowStep.CAPTURE_DOCUMENT);
            } else if (captureDocument != null) {
                final boolean docTypeExists = captureDocument.hasKey("docType");
                final boolean countryCodeExists = captureDocument.hasKey("countryCode");
                if (docTypeExists && countryCodeExists) {
                    String docTypeString = captureDocument.getString("docType");

                    DocumentType docTypeEnum;
                    try {
                        docTypeEnum = DocumentType.valueOf(docTypeString);
                    } catch (IllegalArgumentException iae) {
                        System.err.println("Unexpected docType value: [" + docTypeString + "]");
                        throw new Exception("Unexpected docType value.");
                    }

                    String countryCodeString = captureDocument.getString("countryCode");
                    CountryCode countryCodeEnum = findCountryCodeByAlpha3(countryCodeString);
                    
                    if (countryCodeEnum ==null) {
                        System.err.println("Unexpected countryCode value: [" + countryCodeString + "]");
                        throw new Exception("Unexpected countryCode value.");
                    }

                    flowStepList.add(new CaptureScreenStep(docTypeEnum, countryCodeEnum));
                } else if (!docTypeExists && !countryCodeExists) {
                    flowStepList.add(FlowStep.CAPTURE_DOCUMENT);
                } else {
                    throw new Exception("For countryCode and docType: both must be specified, or both must be omitted.");
                }
            }

            final boolean captureFaceEnabled = flowSteps.hasKey("captureFace");
            final ReadableMap captureFace = captureFaceEnabled ? flowSteps.getMap("captureFace") : null;

            if (captureFace != null) {
                final boolean captureFaceTypeExists = captureFace.hasKey("type");
                if (captureFaceTypeExists) {
                    final String captureFaceType = captureFace.getString("type");
                    if (captureFaceType.equals("PHOTO")) {
                        flowStepList.add(new FaceCaptureStep(FaceCaptureVariant.PHOTO));
                    } else if (captureFaceType.equals("VIDEO")) {
                        flowStepList.add(new FaceCaptureStep(FaceCaptureVariant.VIDEO));
                    } else {
                        throw new Exception("Invalid face capture type.  \"type\" must be VIDEO or PHOTO.");
                    }
                } else {
                    // Default face capture type is photo.
                    flowStepList.add(new FaceCaptureStep(FaceCaptureVariant.PHOTO));
                }
            }
            flowStepList.add(FlowStep.FINAL);

            final FlowStep[] flowStepsWithOptions = flowStepList.toArray(new FlowStep[0]);

            return flowStepsWithOptions;
        } catch (final Exception e) {
            e.printStackTrace();
            // Wrap all unexpected exceptions.
            throw new Exception("Error generating request", e);
        }
    }

    public static CountryCode findCountryCodeByAlpha3(String countryCodeString) {
        CountryCode countryCode = null;
        // We'll use a loop to find the value, because streams are not supported in Java 7.
        for (CountryCode cc : CountryCode.values()) {
            if (cc.getAlpha3().equals(countryCodeString)) {
                countryCode = cc;
            }
        }
        return countryCode;
    }
}
