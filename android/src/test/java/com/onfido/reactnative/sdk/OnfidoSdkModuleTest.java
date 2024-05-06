package com.onfido.reactnative.sdk;

import android.app.Activity;

import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.onfido.android.sdk.capture.DocumentType;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.ui.options.FlowAction;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.ui.options.MotionCaptureVariantOptions;
import com.onfido.android.sdk.capture.ui.options.PhotoCaptureVariantOptions;
import com.onfido.android.sdk.capture.ui.options.VideoCaptureVariantOptions;
import com.onfido.android.sdk.capture.utils.CountryCode;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OnfidoFactory.class)
public class OnfidoSdkModuleTest {

    private OnfidoSdkModule onfidoSdkModule;
    private Promise promiseMock;
    private final Onfido onfidoClientMock = mock(Onfido.class);

    @Before
    public void init() {
        promiseMock = mock(Promise.class);
        OnfidoFactory onfidoFactoryMock = PowerMockito.mock(OnfidoFactory.class);
        ReactApplicationContext reactApplicationContextMock = mock(ReactApplicationContext.class);
        PowerMockito.mockStatic(OnfidoFactory.class);
        given(OnfidoFactory.create(any(ReactApplicationContext.class))).willReturn(onfidoFactoryMock);
        given(onfidoFactoryMock.getClient()).willReturn(onfidoClientMock);
        onfidoSdkModule = new OnfidoSdkModule(reactApplicationContextMock);
    }

    //region Country code

    @Test
    public void shouldFindCountryCode() {
        CountryCode cc = OnfidoSdkModule.findCountryCodeByAlpha2("GB");
        assertEquals(CountryCode.GB, cc);
    }

    @Test
    public void shouldNotFindInvalidCountryCode() {
        CountryCode cc = OnfidoSdkModule.findCountryCodeByAlpha2("12");
        assertNull(cc);
    }

    //endregion

    @Test
    public void shouldReturnTheExpectedModuleName() {
        assertEquals(onfidoSdkModule.getName(), "OnfidoSdk");
    }

    @Test
    public void shouldInitializeUsingTheMockClient() {
        assertEquals(onfidoSdkModule.client, onfidoClientMock);
    }

    @Test
    public void shouldCatchUnexpectedExceptionWhenConfigIsNull() {
        onfidoSdkModule.start(null, promiseMock);
        verify(promiseMock).reject(eq("config_error"), any(Exception.class));
    }

    @Test
    public void shouldTriggerErrorWhenActivityIsNull() {
        final ReadableMap flowStepsMock = mock(ReadableMap.class);
        when(flowStepsMock.hasKey("welcome")).thenReturn(false);
        when(flowStepsMock.getBoolean("captureDocument")).thenReturn(false);
        when(flowStepsMock.hasKey("captureFace")).thenReturn(false);

        ReadableMap configMock = mock(ReadableMap.class);
        when(configMock.getString("sdkToken")).thenReturn("mockSdkToken123");
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        onfidoSdkModule.start(configMock, promiseMock);
        verify(promiseMock).reject(eq("error"), any(Exception.class));
    }

    @Test
    public void shouldCallTheMockClientWithTheExpectedParameters() {
        final ReadableMap flowStepsMock = mock(ReadableMap.class);
        when(flowStepsMock.hasKey("welcome")).thenReturn(false);
        when(flowStepsMock.getBoolean("captureDocument")).thenReturn(false);
        when(flowStepsMock.hasKey("captureFace")).thenReturn(false);

        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);
        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        onfidoSdkModuleSpy.start(configMock, promiseMock);
        verify(onfidoClientMock).startActivityForResult(
                eq(currentActivityMock),
                eq(OnfidoSdkActivityEventListener.checksActivityCode),
                any(OnfidoConfig.class)
        );
    }

    //region Flow Steps

    //region Liveness

    @Test
    public void shouldHaveLivenessWithRecordAudioParameters() {
        // Arrange
        final ReadableMap flowStepsMock = mock(ReadableMap.class);
        when(flowStepsMock.hasKey("welcome")).thenReturn(false);
        when(flowStepsMock.getBoolean("captureDocument")).thenReturn(false);
        when(flowStepsMock.hasKey("captureFace")).thenReturn(true);

        ReadableMap livenessMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(livenessMock.getString("sdkToken")).thenReturn(sdkToken);
        String captureFaceType = "MOTION";

        when(livenessMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        when(flowStepsMock.getMap("captureFace")).thenAnswer(
                (Answer<ReadableMap>) invocation -> {
                    final ReadableMap flowStepsMock1 = mock(ReadableMap.class);

                    when(flowStepsMock1.getBoolean("recordAudio")).thenReturn(true);
                    when(flowStepsMock1.hasKey("recordAudio")).thenReturn(true);
                    when(flowStepsMock1.hasKey("type")).thenReturn(true);
                    when(flowStepsMock1.getString("type")).thenReturn(captureFaceType);
                    when(flowStepsMock1.hasKey("options")).thenReturn(true);
                    return flowStepsMock1;
                });

        Activity currentActivityMock = mock(Activity.class);
        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        // Act
        OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withCustomFlow(new FlowStep[0])
                .build();

        onfidoSdkModuleSpy.start(livenessMock, promiseMock);

        ArgumentCaptor<OnfidoConfig> configCaptor = ArgumentCaptor.forClass(OnfidoConfig.class);

        verify(onfidoClientMock).startActivityForResult(
                eq(currentActivityMock),
                eq(OnfidoSdkActivityEventListener.checksActivityCode),
                configCaptor.capture()
        );

        // Assert
        OnfidoConfig createdConfig = configCaptor.getValue();
        assertNotNull(createdConfig);
        assertNotNull(createdConfig.getFlowSteps());
        assertNotNull(createdConfig.getFlowSteps().get(0));
        FlowStep videoCaptureFlowStep = createdConfig.getFlowSteps().get(0);
        assertEquals(videoCaptureFlowStep.getAction(), FlowAction.ACTIVE_VIDEO_CAPTURE);
        assertNotNull(videoCaptureFlowStep.getOptions());
        MotionCaptureVariantOptions options = (MotionCaptureVariantOptions) videoCaptureFlowStep.getOptions();
        assertNotNull(options);
        assertNotNull(options.getAudioEnabled());
        assertTrue(options.getAudioEnabled());

    }

    //endregion

    @Test
    public void shouldCallTheMockClientWithAllowedDocumentTypesParameters() {
        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);

        JavaOnlyMap map = JavaOnlyMap.of(
                "allowedDocumentTypes", JavaOnlyArray.of("NATIONAL_IDENTITY_CARD", "PASSPORT")
        );
        final ReadableMap flowStepsMock = JavaOnlyMap.of(
                "welcome", false,
                "captureDocument", map
        );
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);

        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        // Act
        OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withCustomFlow(new FlowStep[0])
                .build();

        onfidoSdkModuleSpy.start(configMock, promiseMock);

        ArgumentCaptor<OnfidoConfig> configCaptor = ArgumentCaptor.forClass(OnfidoConfig.class);

        verify(onfidoClientMock).startActivityForResult(
                eq(currentActivityMock),
                eq(OnfidoSdkActivityEventListener.checksActivityCode),
                configCaptor.capture()
        );

        // Assert
        OnfidoConfig createdConfig = configCaptor.getValue();

        assertEquals(
                Arrays.asList(
                        DocumentType.NATIONAL_IDENTITY_CARD, DocumentType.PASSPORT
                ),
                createdConfig.getDocumentTypes()
        );
    }

    @Test
    public void shouldIncludeProofOfAddress() {
        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);

        final ReadableMap flowStepsMock = JavaOnlyMap.of(
                "welcome", false,
                "proofOfAddress", true
        );
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);

        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        // Act
        OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withCustomFlow(new FlowStep[0])
                .build();

        onfidoSdkModuleSpy.start(configMock, promiseMock);

        ArgumentCaptor<OnfidoConfig> configCaptor = ArgumentCaptor.forClass(OnfidoConfig.class);

        verify(onfidoClientMock).startActivityForResult(
                eq(currentActivityMock),
                eq(OnfidoSdkActivityEventListener.checksActivityCode),
                configCaptor.capture()
        );

        // Assert
        OnfidoConfig createdConfig = configCaptor.getValue();

        assertEquals(
                FlowStep.PROOF_OF_ADDRESS,
                createdConfig.getFlowSteps().get(0)
        );
    }

    //region Welcome Screen

    @Test
    public void shouldIncludeWelcomeScreenWhenConfiguredTo_singleFiltering() {
        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);

        final ReadableMap flowStepsMock = JavaOnlyMap.of(
                "welcome", true,
                "captureDocument", mock(ReadableMap.class)
        );
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);

        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        // Act
        OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withCustomFlow(new FlowStep[0])
                .build();

        onfidoSdkModuleSpy.start(configMock, promiseMock);

        ArgumentCaptor<OnfidoConfig> configCaptor = ArgumentCaptor.forClass(OnfidoConfig.class);

        verify(onfidoClientMock).startActivityForResult(
                eq(currentActivityMock),
                eq(OnfidoSdkActivityEventListener.checksActivityCode),
                configCaptor.capture()
        );

        // Assert
        OnfidoConfig createdConfig = configCaptor.getValue();
        List<FlowStep> resultingFlowSteps = createdConfig.getFlowSteps();

        assertNotNull(resultingFlowSteps);
        assertEquals(FlowStep.WELCOME, resultingFlowSteps.get(0));
        assertEquals(FlowStep.CAPTURE_DOCUMENT, resultingFlowSteps.get(1));
    }

    @Test
    public void shouldIncludeWelcomeScreenWhenConfiguredTo_withAllowedDocumentTypesFiltering() {
        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);

        JavaOnlyMap map = JavaOnlyMap.of(
                "allowedDocumentTypes", JavaOnlyArray.of("NATIONAL_IDENTITY_CARD", "PASSPORT")
        );
        final ReadableMap flowStepsMock = JavaOnlyMap.of(
                "welcome", true,
                "captureDocument", map
        );
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);

        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        // Act
        OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withCustomFlow(new FlowStep[0])
                .build();

        onfidoSdkModuleSpy.start(configMock, promiseMock);

        ArgumentCaptor<OnfidoConfig> configCaptor = ArgumentCaptor.forClass(OnfidoConfig.class);

        verify(onfidoClientMock).startActivityForResult(
                eq(currentActivityMock),
                eq(OnfidoSdkActivityEventListener.checksActivityCode),
                configCaptor.capture()
        );

        // Assert
        OnfidoConfig createdConfig = configCaptor.getValue();
        List<FlowStep> resultingFlowSteps = createdConfig.getFlowSteps();

        assertNotNull(resultingFlowSteps);
        assertEquals(FlowStep.WELCOME, resultingFlowSteps.get(0));
        assertEquals(FlowStep.CAPTURE_DOCUMENT, resultingFlowSteps.get(1));
    }

    @Test
    public void shouldHideWelcomeScreenWhenConfiguredTo_singleFiltering() {
        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);

        final ReadableMap flowStepsMock = JavaOnlyMap.of(
                "welcome", false,
                "captureDocument", mock(ReadableMap.class)
        );
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);

        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        // Act
        OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withCustomFlow(new FlowStep[0])
                .build();

        onfidoSdkModuleSpy.start(configMock, promiseMock);

        ArgumentCaptor<OnfidoConfig> configCaptor = ArgumentCaptor.forClass(OnfidoConfig.class);

        verify(onfidoClientMock).startActivityForResult(
                eq(currentActivityMock),
                eq(OnfidoSdkActivityEventListener.checksActivityCode),
                configCaptor.capture()
        );

        // Assert
        OnfidoConfig createdConfig = configCaptor.getValue();
        List<FlowStep> resultingFlowSteps = createdConfig.getFlowSteps();

        assertNotNull(resultingFlowSteps);
        assertEquals(FlowStep.CAPTURE_DOCUMENT, resultingFlowSteps.get(0));
    }

    @Test
    public void shouldHideWelcomeScreenWhenConfiguredTo_withAllowedDocumentTypesFiltering() {
        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);

        JavaOnlyMap map = JavaOnlyMap.of(
                "allowedDocumentTypes", JavaOnlyArray.of("NATIONAL_IDENTITY_CARD", "PASSPORT")
        );
        final ReadableMap flowStepsMock = JavaOnlyMap.of(
                "welcome", false,
                "captureDocument", map
        );
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);

        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        // Act
        OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withCustomFlow(new FlowStep[0])
                .build();

        onfidoSdkModuleSpy.start(configMock, promiseMock);

        ArgumentCaptor<OnfidoConfig> configCaptor = ArgumentCaptor.forClass(OnfidoConfig.class);

        verify(onfidoClientMock).startActivityForResult(
                eq(currentActivityMock),
                eq(OnfidoSdkActivityEventListener.checksActivityCode),
                configCaptor.capture()
        );

        // Assert
        OnfidoConfig createdConfig = configCaptor.getValue();
        List<FlowStep> resultingFlowSteps = createdConfig.getFlowSteps();

        assertNotNull(resultingFlowSteps);
        assertEquals(FlowStep.CAPTURE_DOCUMENT, resultingFlowSteps.get(0));
    }

    //endregion

    @Test
    public void shouldIncludeFlowStepsInTheRightOrder_singleFiltering() {
        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);

        final ReadableMap flowStepsMock = JavaOnlyMap.of(
                "welcome", true,
                "proofOfAddress", true,
                "captureDocument", mock(ReadableMap.class),
                "captureFace", mock(ReadableMap.class)
        );
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);

        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        // Act
        OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withCustomFlow(new FlowStep[0])
                .build();

        onfidoSdkModuleSpy.start(configMock, promiseMock);

        ArgumentCaptor<OnfidoConfig> configCaptor = ArgumentCaptor.forClass(OnfidoConfig.class);

        verify(onfidoClientMock).startActivityForResult(
                eq(currentActivityMock),
                eq(OnfidoSdkActivityEventListener.checksActivityCode),
                configCaptor.capture()
        );

        // Assert
        OnfidoConfig createdConfig = configCaptor.getValue();
        List<FlowStep> resultingFlowSteps = createdConfig.getFlowSteps();
        assertNotNull(resultingFlowSteps);

        // Expected flow steps order: welcome - doc capture - POA - face capture

        assertEquals(FlowStep.WELCOME, resultingFlowSteps.get(0));
        assertEquals(FlowStep.CAPTURE_DOCUMENT, resultingFlowSteps.get(1));
        assertEquals(FlowStep.PROOF_OF_ADDRESS, resultingFlowSteps.get(2));

        FlowStep faceCaptureStep = resultingFlowSteps.get(3);
        assertEquals(FlowAction.CAPTURE_FACE, faceCaptureStep.getAction());
    }

    @Test
    public void shouldIncludeFlowStepsInTheRightOrder_withAllowedDocumentTypesFiltering() {
        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);

        JavaOnlyMap map = JavaOnlyMap.of(
                "allowedDocumentTypes", JavaOnlyArray.of("NATIONAL_IDENTITY_CARD", "PASSPORT")
        );
        final ReadableMap flowStepsMock = JavaOnlyMap.of(
                "welcome", true,
                "proofOfAddress", true,
                "captureDocument", map,
                "captureFace", mock(ReadableMap.class)
        );
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);

        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        // Act
        OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withCustomFlow(new FlowStep[0])
                .build();

        onfidoSdkModuleSpy.start(configMock, promiseMock);

        ArgumentCaptor<OnfidoConfig> configCaptor = ArgumentCaptor.forClass(OnfidoConfig.class);

        verify(onfidoClientMock).startActivityForResult(
                eq(currentActivityMock),
                eq(OnfidoSdkActivityEventListener.checksActivityCode),
                configCaptor.capture()
        );

        // Assert
        OnfidoConfig createdConfig = configCaptor.getValue();
        List<FlowStep> resultingFlowSteps = createdConfig.getFlowSteps();
        assertNotNull(resultingFlowSteps);

        // Expected flow steps order: welcome - doc capture - POA - face capture

        assertEquals(FlowStep.WELCOME, resultingFlowSteps.get(0));
        assertEquals(FlowStep.CAPTURE_DOCUMENT, resultingFlowSteps.get(1));
        assertEquals(FlowStep.PROOF_OF_ADDRESS, resultingFlowSteps.get(2));

        FlowStep faceCaptureStep = resultingFlowSteps.get(3);
        assertEquals(FlowAction.CAPTURE_FACE, faceCaptureStep.getAction());
    }

    //endregion
}
