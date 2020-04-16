package com.onfido.reactnative.sdk;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.onfido.android.sdk.capture.DocumentType;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.errors.OnfidoException;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureVariant;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.upload.Captures;
import com.onfido.android.sdk.capture.upload.DocumentSide;
import com.onfido.android.sdk.capture.upload.Face;
import com.onfido.android.sdk.capture.utils.CountryCode;
import com.onfido.api.client.data.DocSide;
import com.onfido.reactnative.sdk.OnfidoSdkModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Captures.class, ReactNativeBridgeUtiles.class, Captures.Document.class})
public class OnfidoSdkActivityEventListenerTest {

    private Promise promiseMock;
    private Intent intentMock;
    private Captures capturesMock;
    private final Onfido onfidoClientMock = mock(Onfido.class);

    private OnfidoSdkActivityEventListener onfidoSdkActivityEventListener;

    @Before
    public void init() {
        promiseMock = mock(Promise.class);
        intentMock = mock(Intent.class);
        capturesMock = PowerMockito.mock(Captures.class);
        onfidoSdkActivityEventListener = new OnfidoSdkActivityEventListener(onfidoClientMock);
        onfidoSdkActivityEventListener.setCurrentPromise(promiseMock);
        /*
        // Facebook's WritableMap uses platform-specific implementations, so it must be mocked.
        // See http://g.co/androidstudio/not-mocked
        PowerMockito.mockStatic(Arguments.class);
        Answer<Object> answer = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return mock(WritableMap.class);
            }
        };
        PowerMockito.when(Arguments.createMap()).thenAnswer(answer);*/
    }

    @Test
    public void shouldRegisterActivityListener() throws Exception {
        int resultCode = 123;
        onfidoSdkActivityEventListener.onActivityResult(null, 0,resultCode,intentMock);
        verify(onfidoClientMock).handleActivityResult(eq(resultCode), eq(intentMock), any(Onfido.OnfidoResultListener.class));
    }

    @Test
    public void shouldRejectWithCancel() throws Exception {
        int resultCode = 123;
        onfidoSdkActivityEventListener.onActivityResult(null, 0,resultCode,null);
        ArgumentCaptor<Onfido.OnfidoResultListener> resultListenerCaptor = ArgumentCaptor.forClass(Onfido.OnfidoResultListener.class);

        verify(onfidoClientMock).handleActivityResult(eq(resultCode), eq((Intent) null), resultListenerCaptor.capture());
        Onfido.OnfidoResultListener resultListener = resultListenerCaptor.getValue();
        resultListener.userExited(null);

        verify(promiseMock).reject(eq("cancel"), any(Exception.class));
    }

    @Test
    public void shouldRejectWithError() throws Exception {
        int resultCode = 123;
        onfidoSdkActivityEventListener.onActivityResult(null, 0,resultCode,null);
        ArgumentCaptor<Onfido.OnfidoResultListener> resultListenerCaptor = ArgumentCaptor.forClass(Onfido.OnfidoResultListener.class);

        verify(onfidoClientMock).handleActivityResult(eq(resultCode), eq((Intent) null), resultListenerCaptor.capture());
        Onfido.OnfidoResultListener resultListener = resultListenerCaptor.getValue();
        resultListener.onError( new OnfidoException("example message"));

        verify(promiseMock).reject(eq("error"), any(Exception.class));
    }

    @Test
    public void shouldResolveSuccessfully() throws Exception {
        int resultCode = 123;
        onfidoSdkActivityEventListener.onActivityResult(null, 0,resultCode,null);
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        ArgumentCaptor<Onfido.OnfidoResultListener> resultListenerCaptor = ArgumentCaptor.forClass(Onfido.OnfidoResultListener.class);
        PowerMockito.mockStatic(ReactNativeBridgeUtiles.class);
        when(ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap( any(Response.class))).thenReturn(mock(WritableMap.class));
        verify(onfidoClientMock).handleActivityResult(eq(resultCode), eq((Intent) null), resultListenerCaptor.capture());
        Onfido.OnfidoResultListener resultListener = resultListenerCaptor.getValue();

        resultListener.userCompleted(capturesMock);

        verify(promiseMock).resolve(any(WritableMap.class));
        PowerMockito.verifyStatic(ReactNativeBridgeUtiles.class);
        ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap(responseCaptor.capture());

        assertNull(responseCaptor.getValue().face);
        assertNull(responseCaptor.getValue().document);
    }

    @Test
    public void shouldResolveWithFaceSuccessfully() throws Exception {
        int resultCode = 123;
        String faceId = "faceId123";
        FaceCaptureVariant faceVariant = FaceCaptureVariant.PHOTO;
        when(capturesMock.getFace()).thenReturn(new Face(faceId, faceVariant));

        onfidoSdkActivityEventListener.onActivityResult(null, 0,resultCode,null);
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        ArgumentCaptor<Onfido.OnfidoResultListener> resultListenerCaptor = ArgumentCaptor.forClass(Onfido.OnfidoResultListener.class);
        PowerMockito.mockStatic(ReactNativeBridgeUtiles.class);
        when(ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap( any(Response.class))).thenReturn(mock(WritableMap.class));
        verify(onfidoClientMock).handleActivityResult(eq(resultCode), eq((Intent) null), resultListenerCaptor.capture());
        Onfido.OnfidoResultListener resultListener = resultListenerCaptor.getValue();

        resultListener.userCompleted(capturesMock);

        verify(promiseMock).resolve(any(WritableMap.class));
        PowerMockito.verifyStatic(ReactNativeBridgeUtiles.class);
        ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap(responseCaptor.capture());

        assertEquals(responseCaptor.getValue().face.id, faceId);
        assertEquals(responseCaptor.getValue().face.variant, faceVariant.toString());
        assertNull(responseCaptor.getValue().document);
    }


    @Test
    public void shouldResolveWithDocumentSuccessfully() throws Exception {
        int resultCode = 123;
        String docFrontId = "docFrontId123";
        String docBackId = "docBackId123";
        Captures.Document documentMock = PowerMockito.mock(Captures.Document.class);
        when(documentMock.getFront()).thenReturn(new DocumentSide(docFrontId, DocSide.FRONT, DocumentType.DRIVING_LICENCE));
        when(documentMock.getBack()).thenReturn(new DocumentSide(docBackId, DocSide.BACK, DocumentType.DRIVING_LICENCE));
        when(capturesMock.getDocument()).thenReturn(documentMock);

        onfidoSdkActivityEventListener.onActivityResult(null, 0,resultCode,null);
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        ArgumentCaptor<Onfido.OnfidoResultListener> resultListenerCaptor = ArgumentCaptor.forClass(Onfido.OnfidoResultListener.class);
        PowerMockito.mockStatic(ReactNativeBridgeUtiles.class);
        when(ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap( any(Response.class))).thenReturn(mock(WritableMap.class));
        verify(onfidoClientMock).handleActivityResult(eq(resultCode), eq((Intent) null), resultListenerCaptor.capture());
        Onfido.OnfidoResultListener resultListener = resultListenerCaptor.getValue();

        resultListener.userCompleted(capturesMock);

        verify(promiseMock).resolve(any(WritableMap.class));
        PowerMockito.verifyStatic(ReactNativeBridgeUtiles.class);
        ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap(responseCaptor.capture());

        assertEquals(responseCaptor.getValue().document.front.id, docFrontId);
        assertEquals(responseCaptor.getValue().document.back.id, docBackId);
        assertNull(responseCaptor.getValue().face);
    }
}