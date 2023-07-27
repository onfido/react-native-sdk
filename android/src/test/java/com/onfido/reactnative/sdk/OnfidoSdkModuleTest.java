package com.onfido.reactnative.sdk;

import android.app.Activity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
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
import org.mockito.ArgumentMatcher;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.stubbing.Answer;
import org.mockito.invocation.InvocationOnMock;

import com.facebook.react.bridge.ReactApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Test
    public void shouldFindCountryCode() throws Exception {
        CountryCode cc = OnfidoSdkModule.findCountryCodeByAlpha2("GB");
        assertEquals(CountryCode.GB, cc);
    }

    @Test
    public void shouldNotFindInvalidCountryCode() throws Exception {
        CountryCode cc = OnfidoSdkModule.findCountryCodeByAlpha2("12");
        assertNull(cc);
    }

    @Test
    public void shouldReturnTheExpectedModuleName() throws Exception {
        assertEquals(onfidoSdkModule.getName(), "OnfidoSdk");
    }

    @Test
    public void shouldInitializeUsingTheMockClient() throws Exception {
        assertEquals(onfidoSdkModule.client, onfidoClientMock);
    }

    @Test
    public void shouldCatchUnexpectedExceptionWhenConfigIsNull() throws Exception {
        onfidoSdkModule.start(null, promiseMock);
        verify(promiseMock).reject(eq("config_error"), any(Exception.class));
    }

    @Test
    public void shouldTriferErrorWhenActivityIsNull() throws Exception {
        final ReadableMap flowStepsMock = mock(ReadableMap.class);
        when(flowStepsMock.hasKey("welcome")).thenReturn(false);
        when(flowStepsMock.getBoolean("captureDocument")).thenReturn(false);
        when(flowStepsMock.hasKey("captureFace")).thenReturn(false);

        ReadableMap configMock = mock(ReadableMap.class);
        when(configMock.getString("sdkToken")).thenReturn("mockSdkToken123");
        when(configMock.getMap("flowSteps")).thenAnswer(new Answer<ReadableMap>() {
            public ReadableMap answer(InvocationOnMock invocation) throws Throwable {
                return flowStepsMock;
            }
        });

        onfidoSdkModule.start(configMock, promiseMock);
        verify(promiseMock).reject(eq("error"), any(Exception.class));
    }

    @Test
    public void shouldCallTheMockClientWithTheExpectedParameters() throws Exception {
        final ReadableMap flowStepsMock = mock(ReadableMap.class);
        when(flowStepsMock.hasKey("welcome")).thenReturn(false);
        when(flowStepsMock.getBoolean("captureDocument")).thenReturn(false);
        when(flowStepsMock.hasKey("captureFace")).thenReturn(false);

        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);
        when(configMock.getMap("flowSteps")).thenAnswer(new Answer<ReadableMap>() {
            public ReadableMap answer(InvocationOnMock invocation) throws Throwable {
                return flowStepsMock;
            }
        });

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);
        OnfidoSdkModule onfidoSdkModuleSpy = spy(onfidoSdkModule);
        when(onfidoSdkModuleSpy.getCurrentActivityInParentClass()).thenReturn(currentActivityMock);

        final OnfidoConfig onfidoConfigExpected = OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withCustomFlow(new FlowStep[0])
                .build();

        onfidoSdkModuleSpy.start(configMock, promiseMock);
        verify(onfidoClientMock).startActivityForResult(eq(currentActivityMock), eq(OnfidoSdkActivityEventListener.checksActivityCode), any(OnfidoConfig.class));
    }

    @Test
    public void shouldHaveLivenessWithVideoFallbackParameters() throws Exception {
        // Arrange
        final ReadableMap flowStepsMock = mock(ReadableMap.class);
        when(flowStepsMock.hasKey("welcome")).thenReturn(false);
        when(flowStepsMock.getBoolean("captureDocument")).thenReturn(false);
        when(flowStepsMock.hasKey("captureFace")).thenReturn(true);

        ReadableMap livenessMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(livenessMock.getString("sdkToken")).thenReturn(sdkToken);
        String captureFaceType = "MOTION";

        when(livenessMock.getMap("flowSteps")).thenAnswer(new Answer<ReadableMap>() {
            public ReadableMap answer(InvocationOnMock invocation) throws Throwable {
                return flowStepsMock;
            }
        });

        when(flowStepsMock.getMap("captureFace")).thenAnswer(new Answer<ReadableMap>() {
            public ReadableMap answer(InvocationOnMock invocation) throws Throwable {
                final ReadableMap flowStepsMock = mock(ReadableMap.class);

                when(flowStepsMock.hasKey("motionCaptureFallback")).thenReturn(true);
                when(flowStepsMock.getMap("motionCaptureFallback")).thenAnswer(new Answer<ReadableMap>() {
                    public ReadableMap answer(InvocationOnMock invocation) throws Throwable {
                        final ReadableMap motionCaptureFallbackMock = mock(ReadableMap.class);
                        when(motionCaptureFallbackMock.hasKey("type")).thenReturn(true);
                        when(motionCaptureFallbackMock.getString("type")).thenReturn("VIDEO");
                        when(motionCaptureFallbackMock.hasKey("showIntro")).thenReturn(true);
                        when(motionCaptureFallbackMock.getBoolean("showIntro")).thenReturn(false);
                        when(motionCaptureFallbackMock.hasKey("showConfirmation")).thenReturn(true);
                        when(motionCaptureFallbackMock.getBoolean("showConfirmation")).thenReturn(false);
                        return motionCaptureFallbackMock;
                    }
                });

                when(flowStepsMock.hasKey("type")).thenReturn(true);
                when(flowStepsMock.getString("type")).thenReturn(captureFaceType);

                return flowStepsMock;
            }
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

        verify(onfidoClientMock).startActivityForResult(eq(currentActivityMock), eq(OnfidoSdkActivityEventListener.checksActivityCode), configCaptor.capture());

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
        assertNotNull(options.getCaptureFallback());
        assertEquals(options.getCaptureFallback().getAction(), FlowAction.CAPTURE_LIVENESS);
        VideoCaptureVariantOptions videoOptions = (VideoCaptureVariantOptions) options.getCaptureFallback().getOptions();
        assertNotNull(videoOptions);
        assertFalse(videoOptions.getShowIntroVideo());
        assertFalse(videoOptions.getShowConfirmationVideo());
    }

    @Test
    public void shouldHaveLivenessWithPhotoFallbackParameters() throws Exception {
        // Arrange
        final ReadableMap flowStepsMock = mock(ReadableMap.class);
        when(flowStepsMock.hasKey("welcome")).thenReturn(false);
        when(flowStepsMock.getBoolean("captureDocument")).thenReturn(false);
        when(flowStepsMock.hasKey("captureFace")).thenReturn(true);

        ReadableMap livenessMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(livenessMock.getString("sdkToken")).thenReturn(sdkToken);
        String captureFaceType = "MOTION";

        when(livenessMock.getMap("flowSteps")).thenAnswer(new Answer<ReadableMap>() {
            public ReadableMap answer(InvocationOnMock invocation) throws Throwable {
                return flowStepsMock;
            }
        });

        when(flowStepsMock.getMap("captureFace")).thenAnswer(new Answer<ReadableMap>() {
            public ReadableMap answer(InvocationOnMock invocation) throws Throwable {
                final ReadableMap flowStepsMock = mock(ReadableMap.class);

                when(flowStepsMock.hasKey("motionCaptureFallback")).thenReturn(true);
                when(flowStepsMock.getMap("motionCaptureFallback")).thenAnswer(new Answer<ReadableMap>() {
                    public ReadableMap answer(InvocationOnMock invocation) throws Throwable {
                        final ReadableMap motionCaptureFallbackMock = mock(ReadableMap.class);
                        when(motionCaptureFallbackMock.hasKey("type")).thenReturn(true);
                        when(motionCaptureFallbackMock.getString("type")).thenReturn("PHOTO");
                        when(motionCaptureFallbackMock.hasKey("showIntro")).thenReturn(true);
                        when(motionCaptureFallbackMock.getBoolean("showIntro")).thenReturn(false);
                        return motionCaptureFallbackMock;
                    }
                });

                when(flowStepsMock.hasKey("type")).thenReturn(true);
                when(flowStepsMock.getString("type")).thenReturn(captureFaceType);
                when(flowStepsMock.hasKey("options")).thenReturn(true);
                return flowStepsMock;
            }
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

        verify(onfidoClientMock).startActivityForResult(eq(currentActivityMock), eq(OnfidoSdkActivityEventListener.checksActivityCode), configCaptor.capture());

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
        assertNotNull(options.getCaptureFallback());
        assertEquals(options.getCaptureFallback().getAction(), FlowAction.CAPTURE_FACE);
        PhotoCaptureVariantOptions videoOptions = (PhotoCaptureVariantOptions) options.getCaptureFallback().getOptions();
        assertNotNull(videoOptions);
        assertFalse(videoOptions.getWithIntroScreen());
    }

    @Test
    public void shouldHaveLivenessWithRecordAudioParameters() throws Exception {
        // Arrange
        final ReadableMap flowStepsMock = mock(ReadableMap.class);
        when(flowStepsMock.hasKey("welcome")).thenReturn(false);
        when(flowStepsMock.getBoolean("captureDocument")).thenReturn(false);
        when(flowStepsMock.hasKey("captureFace")).thenReturn(true);

        ReadableMap livenessMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(livenessMock.getString("sdkToken")).thenReturn(sdkToken);
        String captureFaceType = "MOTION";

        when(livenessMock.getMap("flowSteps")).thenAnswer(new Answer<ReadableMap>() {
            public ReadableMap answer(InvocationOnMock invocation) throws Throwable {
                return flowStepsMock;
            }
        });

        when(flowStepsMock.getMap("captureFace")).thenAnswer(new Answer<ReadableMap>() {
            public ReadableMap answer(InvocationOnMock invocation) throws Throwable {
                final ReadableMap flowStepsMock = mock(ReadableMap.class);

                when(flowStepsMock.getBoolean("recordAudio")).thenReturn(true);
                when(flowStepsMock.hasKey("recordAudio")).thenReturn(true);
                when(flowStepsMock.hasKey("type")).thenReturn(true);
                when(flowStepsMock.getString("type")).thenReturn(captureFaceType);
                when(flowStepsMock.hasKey("options")).thenReturn(true);
                return flowStepsMock;
            }
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

        verify(onfidoClientMock).startActivityForResult(eq(currentActivityMock), eq(OnfidoSdkActivityEventListener.checksActivityCode), configCaptor.capture());

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

    @Test
    public void shouldCallTheMockClientWithAllowedDocumentTypesParameters() throws Exception {
        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);

        JavaOnlyMap map = JavaOnlyMap.of("allowedDocumentTypes", JavaOnlyArray.of(
                "NATIONAL_IDENTITY_CARD"
        ));
        final ReadableMap flowStepsMock = JavaOnlyMap.of(
                "welcome", false,
                "captureDocument", map
        );
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);

        final OnfidoConfig onfidoConfigExpected = OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withAllowedDocumentTypes(Collections.singletonList(DocumentType.NATIONAL_IDENTITY_CARD))
                .build();

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
                Arrays.asList(DocumentType.NATIONAL_IDENTITY_CARD),
                createdConfig.getDocumentTypes()
        );
    }

    @Test
    public void shouldIncludeProofOfAddress() throws Exception {
        ReadableMap configMock = mock(ReadableMap.class);
        String sdkToken = "mockSdkToken123";
        when(configMock.getString("sdkToken")).thenReturn(sdkToken);

        JavaOnlyMap map = JavaOnlyMap.of("allowedDocumentTypes", JavaOnlyArray.of(
                "NATIONAL_IDENTITY_CARD"
        ));
        final ReadableMap flowStepsMock = JavaOnlyMap.of(
                "welcome", false,
                "proofOfAddress", true
        );
        when(configMock.getMap("flowSteps")).thenAnswer(
                (Answer<ReadableMap>) invocation -> flowStepsMock
        );

        // Use a spy to mock the internal call to getCurrentActivity
        Activity currentActivityMock = mock(Activity.class);

        final OnfidoConfig onfidoConfigExpected = OnfidoConfig.builder(currentActivityMock)
                .withSDKToken(sdkToken)
                .withAllowedDocumentTypes(Collections.singletonList(DocumentType.NATIONAL_IDENTITY_CARD))
                .build();

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
}
