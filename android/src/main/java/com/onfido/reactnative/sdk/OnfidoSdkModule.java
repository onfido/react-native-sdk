package com.onfido.reactnative.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.facebook.react.bridge.NoSuchKeyException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UnexpectedNativeTypeException;
import com.facebook.react.module.annotations.ReactModule;
import com.onfido.android.sdk.capture.DocumentType;
import com.onfido.android.sdk.capture.EnterpriseFeatures;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.errors.EnterpriseFeatureNotEnabledException;
import com.onfido.android.sdk.capture.errors.EnterpriseFeaturesInvalidLogoCobrandingException;
import com.onfido.android.sdk.capture.errors.EnterpriseFeatureNotEnabledException;
import com.onfido.android.sdk.capture.errors.EnterpriseFeaturesInvalidLogoCobrandingException;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureStep;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureVariant;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureVariantPhoto;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureVariantVideo;
import com.onfido.android.sdk.capture.ui.camera.face.stepbuilder.FaceCaptureStepBuilder;
import com.onfido.android.sdk.capture.ui.options.CaptureScreenStep;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.utils.CountryCode;
import com.onfido.workflow.OnfidoWorkflow;
import com.onfido.workflow.WorkflowConfig;

import java.util.ArrayList;
import java.util.List;

@ReactModule(name = OnfidoSdkModule.NAME)
public class OnfidoSdkModule extends NativeOnfidoModuleSpec {

    public static final String NAME = "OnfidoSdk";

    /* package */ final Onfido client;
    private Promise currentPromise = null;
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
                    workflowSDKConfiguration(currentActivity, workflowRunId, sdkToken);
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
    private void workflowSDKConfiguration(Activity currentActivity, String workflowRunId, String sdkToken) {
        final OnfidoWorkflow flow = OnfidoWorkflow.create(currentActivity);
        this.activityEventListener.setWorkflow(flow);

        flow.startActivityForResult(currentActivity,
                OnfidoSdkActivityEventListener.workflowActivityCode,
                new WorkflowConfig.Builder(sdkToken, workflowRunId).build());
    }

    private void defaultSDKConfiguration(final ReadableMap config, Activity currentActivity, String sdkToken) throws Exception {
        final FlowStep[] flowStepsWithOptions = getFlowStepsFromConfig(config);
                /* Native SDK seems to have a bug that if an empty EnterpriseFeatures is passed to it,
                 the logo will still be hidden, even if explicitly set to false */
        EnterpriseFeatures.Builder enterpriseFeaturesBuilder = EnterpriseFeatures.builder();
        boolean hasSetEnterpriseFeatures = false;

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
                return;
            }
            enterpriseFeaturesBuilder.withCobrandingLogo(cobrandLogoLight, cobrandLogoDark);
            hasSetEnterpriseFeatures = true;
        }

        OnfidoConfig.Builder onfidoConfigBuilder = OnfidoConfig.builder(currentActivity)
                .withSDKToken(sdkToken)
                .withCustomFlow(flowStepsWithOptions);

        if (hasSetEnterpriseFeatures) {
            onfidoConfigBuilder.withEnterpriseFeatures(enterpriseFeaturesBuilder.build());
        }

        if(getBooleanFromConfig(config, "enableNFC")) {
            onfidoConfigBuilder.withNFCReadFeature();
        }

        client.startActivityForResult(currentActivity,
                OnfidoSdkActivityEventListener.checksActivityCode,
                onfidoConfigBuilder.build());
    }

    public static String getSdkTokenFromConfig(final ReadableMap config) {
        return config.getString("sdkToken");
    }

    public static String getWorkflowRunIdFromConfig(final ReadableMap config) {
        final String key = "workflowRunId";
        return config.hasKey(key) ? config.getString(key) : "";
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
                final boolean countryCodeExists = captureDocument.hasKey("alpha2CountryCode");
                if (docTypeExists && countryCodeExists) {
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
                        flowStepList.add(new FaceCaptureStep(new FaceCaptureVariantPhoto()));
                    } else if (captureFaceType.equals("VIDEO")) {
                        flowStepList.add(new FaceCaptureStep(new FaceCaptureVariantVideo()));
                    } else if (captureFaceType.equals("MOTION")) {
                        flowStepList.add(FaceCaptureStepBuilder.forMotion().build());
                    } else {
                        throw new Exception("Invalid face capture type.  \"type\" must be VIDEO or PHOTO.");
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
}
