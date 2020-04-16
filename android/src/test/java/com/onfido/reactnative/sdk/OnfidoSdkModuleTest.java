package com.onfido.reactnative.sdk;

import android.app.Activity;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.utils.CountryCode;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import org.mockito.ArgumentMatcher;
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
        CountryCode cc = OnfidoSdkModule.findCountryCodeByAlpha3("GBR");
        assertEquals(CountryCode.GB, cc);
    }

    @Test
    public void shouldNotFindInvalidCountryCode() throws Exception {
        CountryCode cc = OnfidoSdkModule.findCountryCodeByAlpha3("123");
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
        when(configMock.getMap("flowSteps")).thenAnswer(new Answer<ReadableMap> () {
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
        when(configMock.getMap("flowSteps")).thenAnswer(new Answer<ReadableMap> () {
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
        verify(onfidoClientMock).startActivityForResult(eq(currentActivityMock), eq(1), any(OnfidoConfig.class));


        verify(onfidoClientMock).startActivityForResult(eq(currentActivityMock), eq(1), argThat(new ArgumentMatcher<OnfidoConfig>() {
            @Override
            public boolean matches(OnfidoConfig config) {

                return config.getFlowSteps().size() == 1 && config.getFlowSteps().get(0).equals(FlowStep.FINAL);
            }
        }));
    }

}