package com.onfido.reactnative.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.onfido.android.sdk.capture.DocumentType;
import com.onfido.android.sdk.capture.EnterpriseFeatures;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.config.MediaCallback;
import com.onfido.android.sdk.capture.errors.EnterpriseFeatureNotEnabledException;
import com.onfido.android.sdk.capture.errors.EnterpriseFeaturesInvalidLogoCobrandingException;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureStep;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureVariantPhoto;
import com.onfido.android.sdk.capture.ui.camera.face.stepbuilder.FaceCaptureStepBuilder;
import com.onfido.android.sdk.capture.ui.camera.face.stepbuilder.PhotoCaptureStepBuilder;
import com.onfido.android.sdk.capture.ui.camera.face.stepbuilder.VideoCaptureStepBuilder;
import com.onfido.android.sdk.capture.ui.camera.face.stepbuilder.MotionCaptureStepBuilder;
import com.onfido.android.sdk.capture.ui.options.CaptureScreenStep;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.utils.CountryCode;
import com.onfido.android.sdk.capture.OnfidoTheme;
import com.onfido.workflow.OnfidoWorkflow;
import com.onfido.workflow.WorkflowConfig;

import java.util.ArrayList;
import java.util.List;

// Analytics to be re-added once payloads are harmonised across platforms
enum CallbackType {
    MEDIA
}

@ReactModule(name = OnfidoSdkModule.NAME)
public class OnfidoSdkModule extends NativeOnfidoModuleSpec {

    public static final String NAME = "RNOnfidoSdk";

    /* package */ final Onfido client;
    private Promise currentPromise = null;
    List<CallbackType> callbackTypeList = new ArrayList<CallbackType>();
    private final OnfidoSdkActivityEventListener activityEventListener;

    public OnfidoSdkModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        this.client = OnfidoFactory.create(reactContext).getClient();
        activityEventListener = new OnfidoSdkActivityEventListener(client);
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
        return NAME;
    }

    /**
     * NOTE: This indirection is used to allow unit tests to mock this method
     */
    protected Activity getCurrentActivityInParentClass() {
        return super.getCurrentActivity();
    }

    @ReactMethod
    @Override
    public void start(final ReadableMap config, final Promise promise) {

        setPromise(promise);

        try {
            final String sdkToken;
            try {
                sdkToken = getSdkTokenFromConfig(config);
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
                final String workflowRunId = getWorkflowRunIdFromConfig(config);

                if (!workflowRunId.isEmpty()) {
                    workflowSDKConfiguration(config, currentActivity, sdkToken);
                } else {
                    defaultSDKConfiguration(config, currentActivity, sdkToken);
                }
            } catch (final EnterpriseFeaturesInvalidLogoCobrandingException e) {
                currentPromise.reject("error", new EnterpriseFeaturesInvalidLogoCobrandingException());
                currentPromise = null;
            } catch (final EnterpriseFeatureNotEnabledException e) {
                currentPromise.reject("error", new EnterpriseFeatureNotEnabledException("logoCobrand"));
                currentPromise = null;
            } catch (final Exception e) {
                currentPromise.reject("error", new Exception(e.getMessage(), e));
                currentPromise = null;
            }

        } catch (final Exception e) {
            e.printStackTrace();
            // Wrap all unexpected exceptions.
            currentPromise.reject("error", new Exception("Unexpected error starting Onfido page", e));
            currentPromise = null;
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void workflowSDKConfiguration(final ReadableMap config, Activity currentActivity, String sdkToken) throws Exception {
        final String workflowRunId = getWorkflowRunIdFromConfig(config);

        final OnfidoWorkflow flow = OnfidoWorkflow.create(currentActivity);

        WorkflowConfig.Builder onfidoConfigBuilder = new WorkflowConfig.Builder(sdkToken, workflowRunId);

        EnterpriseFeatures.Builder enterpriseFeaturesBuilder = getEnterpriseFeatures(config);
        if (enterpriseFeaturesBuilder != null) {
            onfidoConfigBuilder.withEnterpriseFeatures(enterpriseFeaturesBuilder.build());
        }

        if (callbackTypeList.contains(CallbackType.MEDIA)) {
            onfidoConfigBuilder.withMediaCallback(addMediaCallback());
        }

        OnfidoTheme onfidoTheme = getThemeFromConfig(config);
        if (onfidoTheme != null) {
            onfidoConfigBuilder.withTheme(onfidoTheme);
        }

        flow.startActivityForResult(currentActivity,
                OnfidoSdkActivityEventListener.workflowActivityCode,
                onfidoConfigBuilder.build()
        );
    }

    private void defaultSDKConfiguration(final ReadableMap config, Activity currentActivity, String sdkToken) throws Exception {
                /* Native SDK seems to have a bug that if an empty EnterpriseFeatures is passed to it,
                 the logo will still be hidden, even if explicitly set to false */

        OnfidoConfig.Builder onfidoConfigBuilder = OnfidoConfig.builder(currentActivity)
                .withSDKToken(sdkToken);

        final FlowStep[] flowStepsWithOptions = getFlowStepsFromConfig(config, onfidoConfigBuilder);

        if (flowStepsWithOptions.length != 0) {
            onfidoConfigBuilder.withCustomFlow(flowStepsWithOptions);
        }

        EnterpriseFeatures.Builder enterpriseFeaturesBuilder = getEnterpriseFeatures(config);
        if (enterpriseFeaturesBuilder != null) {
            onfidoConfigBuilder.withEnterpriseFeatures(enterpriseFeaturesBuilder.build());
        }

        if (callbackTypeList.contains(CallbackType.MEDIA)) {
            onfidoConfigBuilder.withMediaCallback(addMediaCallback());
        }

        if (getBooleanFromConfig(config, "disableNFC")) {
            onfidoConfigBuilder.disableNFC();
        }

        OnfidoTheme onfidoTheme = getThemeFromConfig(config);
        if (onfidoTheme != null) {
            onfidoConfigBuilder.withTheme(onfidoTheme);
        }

        client.startActivityForResult(currentActivity,
                OnfidoSdkActivityEventListener.checksActivityCode,
                onfidoConfigBuilder.build());
    }

    private EnterpriseFeatures.Builder getEnterpriseFeatures(final ReadableMap config) {
        EnterpriseFeatures.Builder enterpriseFeaturesBuilder = EnterpriseFeatures.builder();
        boolean hasSetEnterpriseFeatures = false;
        Activity currentActivity = getCurrentActivityInParentClass();

        if (getBooleanFromConfig(config, "hideLogo")) {
            enterpriseFeaturesBuilder.withHideOnfidoLogo(true);
            hasSetEnterpriseFeatures = true;
        } else if (getBooleanFromConfig(config, "logoCobrand")) {
            int cobrandLogoLight = currentActivity.getApplicationContext().getResources().getIdentifier(
                    "cobrand_logo_light",
                    "drawable",
                    currentActivity.getApplicationContext().getPackageName()
            );
            int cobrandLogoDark = currentActivity.getApplicationContext().getResources().getIdentifier(
                    "cobrand_logo_dark",
                    "drawable",
                    currentActivity.getApplicationContext().getPackageName()
            );
            if (cobrandLogoLight == 0 || cobrandLogoDark == 0) {
                currentPromise.reject("error", new Exception("Cobrand logos were not found"));
                currentPromise = null;
                return null;
            }
            enterpriseFeaturesBuilder.withCobrandingLogo(cobrandLogoLight, cobrandLogoDark);
            hasSetEnterpriseFeatures = true;
        }
        if (getBooleanFromConfig(config, "disableMobileSdkAnalytics")) {
            enterpriseFeaturesBuilder.disableMobileSdkAnalytics();
            hasSetEnterpriseFeatures = true;
        }

        return hasSetEnterpriseFeatures ? enterpriseFeaturesBuilder : null;
    }

    public static String getSdkTokenFromConfig(final ReadableMap config) {
        return config.getString("sdkToken");
    }

    public static String getWorkflowRunIdFromConfig(final ReadableMap config) {
        final String key = "workflowRunId";
        return config.hasKey(key) ? config.getString(key) : "";
    }

    public static OnfidoTheme getThemeFromConfig(final ReadableMap config) throws Exception {
        String themeString = config.getString("theme");
        if (themeString == null) {
            return null;
        }
        OnfidoTheme onfidoTheme;
        try {
            onfidoTheme = OnfidoTheme.valueOf(themeString);
        } catch (Exception e) {
            System.err.println("Unexpected theme value: [" + themeString + "]");
            throw new Exception("Unexpected theme value");
        }
        return onfidoTheme;
    }

    /*
        (!) Please note that flow steps must be presented in a specific order, one which is also
        implemented in the native SDKs, as well as in the iOS RN SDK.

        As per the Product indications in https://onfido.atlassian.net/browse/SDK-2390, this order
        should be: Welcome->Doc->POA->Bio
     */
    public static FlowStep[] getFlowStepsFromConfig(
            final ReadableMap config,
            OnfidoConfig.Builder configBuilder
    ) throws Exception {
        try {
            final ReadableMap flowSteps = config.getMap("flowSteps");

            final boolean welcomePageIsIncluded;
            if (flowSteps.hasKey("welcome")) {
                welcomePageIsIncluded = flowSteps.getBoolean("welcome");
            } else {
                welcomePageIsIncluded = false;
            }

            final boolean proofOfAddress;
            if (flowSteps.hasKey("proofOfAddress")) {
                proofOfAddress = flowSteps.getBoolean("proofOfAddress");
            } else {
                proofOfAddress = false;
            }

            final List<FlowStep> flowStepList = new ArrayList<>();

            if (welcomePageIsIncluded) {
                flowStepList.add(FlowStep.WELCOME);
            }

            if (flowSteps.hasKey("captureDocument")) {
                extractCaptureDocumentStep(flowSteps, flowStepList, configBuilder);
            }

            if (proofOfAddress) {
                flowStepList.add(FlowStep.PROOF_OF_ADDRESS);
            }

            final boolean captureFaceEnabled = flowSteps.hasKey("captureFace");
            final ReadableMap captureFace = captureFaceEnabled ? flowSteps.getMap("captureFace") : null;

            if (captureFace != null) {
                final boolean captureFaceTypeExists = captureFace.hasKey("type");
                if (captureFaceTypeExists) {
                    final String captureFaceType = captureFace.getString("type");
                    switch (captureFaceType) {
                        case "PHOTO":
                            flowStepList.add(faceStepFromPhotoDefinition(captureFace));
                            break;
                        case "VIDEO":
                            flowStepList.add(faceStepFromVideoDefinition(captureFace));
                            break;
                        case "MOTION":
                            flowStepList.add(faceStepFromMotionDefinition(captureFace));
                            break;
                        default:
                            throw new Exception("Invalid face capture type. \"type\" must be VIDEO or PHOTO.");
                    }
                } else {
                    // Default face capture type is photo.
                    flowStepList.add(new FaceCaptureStep(new FaceCaptureVariantPhoto()));
                }
            }

            final FlowStep[] flowStepsWithOptions = flowStepList.toArray(new FlowStep[0]);
            return flowStepsWithOptions;
        } catch (final Exception e) {
            e.printStackTrace();
            // Wrap all unexpected exceptions.
            throw new Exception("Error generating request", e);
        }
    }

    private static void extractCaptureDocumentStep(
            ReadableMap flowSteps,
            List<FlowStep> flowStepList,
            OnfidoConfig.Builder configBuilder
    ) throws Exception {
        ReadableMap captureDocument = flowSteps.getMap("captureDocument");
        if (captureDocument == null) {
            return;
        }
        extractDocumentCaptureDetails(captureDocument, flowStepList, configBuilder);
    }

    private static void extractDocumentCaptureDetails(
            ReadableMap captureDocument,
            List<FlowStep> flowStepList,
            OnfidoConfig.Builder configBuilder
    ) throws Exception {
        final boolean docTypeExists = captureDocument.hasKey("docType");
        final boolean countryCodeExists = captureDocument.hasKey("alpha2CountryCode");
        final boolean withAllowedDocumentTypes = captureDocument.hasKey("allowedDocumentTypes");

        // Validation: incorrect config - 2 filtering ways provided
        if (withAllowedDocumentTypes && countryCodeExists && docTypeExists) {
            throw new IllegalArgumentException("We can either filter the documents on DocumentSelection screen, or skip the selection and go directly to capture");
        }

        // Case 1: no filtering provided => showing general Doc Capture
        if (!docTypeExists && !countryCodeExists && !withAllowedDocumentTypes) {
            flowStepList.add(FlowStep.CAPTURE_DOCUMENT);
            return;
        }

        // Case 2: filtering for one document, one country
        if (docTypeExists && countryCodeExists) {
            extractDocTypeAndCountryForCaptureStep(captureDocument, flowStepList);
            return;
        }

        // Case 3: filtering for multiple documents
        if (withAllowedDocumentTypes) {
            extractAllowedDocumentTypes(captureDocument, configBuilder);
            flowStepList.add(FlowStep.CAPTURE_DOCUMENT);
            return;
        }

        throw new Exception("For countryCode and docType: both must be specified, or both must be omitted.");
    }

    private static void extractAllowedDocumentTypes(
            ReadableMap captureDocument,
            OnfidoConfig.Builder configBuilder
    ) {
        ReadableArray documentTypes = captureDocument.getArray("allowedDocumentTypes");
        ArrayList<DocumentType> types = new ArrayList<>();

        if (documentTypes != null) {
            for (int i = 0; i < documentTypes.size(); i++) {
                types.add(DocumentType.valueOf(documentTypes.getString(i)));
            }
        }
        configBuilder.withAllowedDocumentTypes(types);
    }

    private static void extractDocTypeAndCountryForCaptureStep(
            ReadableMap captureDocument,
            List<FlowStep> flowStepList
    ) throws Exception {
        String docTypeString = captureDocument.getString("docType");

        DocumentType docTypeEnum;
        try {
            docTypeEnum = DocumentType.valueOf(docTypeString);
        } catch (IllegalArgumentException iae) {
            System.err.println("Unexpected docType value: [" + docTypeString + "]");
            throw new Exception("Unexpected docType value.");
        }

        String countryCodeString = captureDocument.getString("alpha2CountryCode");
        CountryCode countryCodeEnum = findCountryCodeByAlpha2(countryCodeString);

        if (countryCodeEnum == null) {
            System.err.println("Unexpected countryCode value: [" + countryCodeString + "]");
            throw new Exception("Unexpected countryCode value.");
        }

        flowStepList.add(new CaptureScreenStep(docTypeEnum, countryCodeEnum));
    }

    private static PhotoCaptureStepBuilder faceStepFromPhotoDefinitionBuilder(ReadableMap definition) {
        final PhotoCaptureStepBuilder builder = FaceCaptureStepBuilder.forPhoto();
        if (definition.hasKey("showIntro")) {
            builder.withIntro(definition.getBoolean("showIntro"));
        }
        return builder;
    }

    private static FlowStep faceStepFromPhotoDefinition(ReadableMap definition) {
        return faceStepFromPhotoDefinitionBuilder(definition).build();
    }

    private static VideoCaptureStepBuilder faceStepFromVideoDefinitionBuilder(ReadableMap definition) {
        final VideoCaptureStepBuilder builder = FaceCaptureStepBuilder.forVideo();
        if (definition.hasKey("showIntro")) {
            builder.withIntro(definition.getBoolean("showIntro"));
        }
        if (definition.hasKey("showConfirmation")) {
            builder.withConfirmationVideoPreview(definition.getBoolean("showConfirmation"));
        }
        return builder;
    }

    private static FlowStep faceStepFromVideoDefinition(ReadableMap definition) {
        return faceStepFromVideoDefinitionBuilder(definition).build();
    }

    private static FlowStep faceStepFromMotionDefinition(ReadableMap definition) {
        final MotionCaptureStepBuilder builder = FaceCaptureStepBuilder.forMotion();
        if (definition.hasKey("recordAudio")) {
            builder.withAudio(definition.getBoolean("recordAudio"));
        }
        if (definition.hasKey("motionCaptureFallback")) {
            final ReadableMap captureFaceFallbackOptions = definition.getMap("motionCaptureFallback");
            final String fallbackType = captureFaceFallbackOptions.getString("type");
            if (fallbackType.equalsIgnoreCase("VIDEO")) {
                builder.withCaptureFallback(faceStepFromVideoDefinitionBuilder(captureFaceFallbackOptions));
            } else if (fallbackType.equalsIgnoreCase("PHOTO")) {
                builder.withCaptureFallback(faceStepFromPhotoDefinitionBuilder(captureFaceFallbackOptions));
            }
        }

        return builder.build();
    }

    public static CountryCode findCountryCodeByAlpha2(String countryCodeString) {
        CountryCode countryCode = null;
        // We'll use a loop to find the value, because streams are not supported in Java 7.
        for (CountryCode cc : CountryCode.values()) {
            if (cc.name().equals(countryCodeString)) {
                countryCode = cc;
            }
        }
        return countryCode;
    }

    private boolean getBooleanFromConfig(ReadableMap config, String key) {
        return config.hasKey(key) && config.getBoolean(key);
    }

    //region Callbacks

    @ReactMethod
    public void addListener(String type) {
        // Keep: Required for RN build in the Event Emitter Calls
    }

    @ReactMethod
    public void removeListeners(double type) {
        // Keep: Required for RN build in the Event Emitter Calls
    }

    private void sendEvent(String name, WritableMap map) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(name, map);
    }

    //region Media

    /**
     * This is a pre-requisite: make sure you call this method before the start method **if** you want to use custom media callbacks
     */
    @ReactMethod
    public void withMediaCallbacksEnabled() {
        callbackTypeList.add(CallbackType.MEDIA);
    }

    private MediaCallback addMediaCallback() {
        return mediaResult -> {
            WritableMap map = ReactNativeBridgeUtiles.getMediaResultMap(mediaResult);
            sendEvent("onfidoMediaCallback", map);
        };
    }

    //endregion
}
