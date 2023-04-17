package com.onfido.reactnative.sdk;

import static com.onfido.reactnative.sdk.OnfidoSdkActivityEventListener.checksActivityCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.onfido.android.sdk.capture.DocumentType;
import com.onfido.android.sdk.capture.ExitCode;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.errors.OnfidoException;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureVariant;
import com.onfido.android.sdk.capture.upload.Captures;
import com.onfido.android.sdk.capture.upload.Document;
import com.onfido.android.sdk.capture.upload.DocumentSide;
import com.onfido.android.sdk.capture.upload.Face;
import com.onfido.api.client.data.DocSide;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Captures.class, ReactNativeBridgeUtiles.class, Document.class})
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
        onfidoSdkActivityEventListener.onActivityResult(null, checksActivityCode, resultCode, intentMock);
        verify(onfidoClientMock).handleActivityResult(eq(resultCode), eq(intentMock), any(Onfido.OnfidoResultListener.class));
    }

    @Test
    public void shouldRejectWithCancel() throws Exception {
        int resultCode = 123;
        onfidoSdkActivityEventListener.onActivityResult(null, checksActivityCode, resultCode, null);
        ArgumentCaptor<Onfido.OnfidoResultListener> resultListenerCaptor = ArgumentCaptor.forClass(Onfido.OnfidoResultListener.class);

        verify(onfidoClientMock).handleActivityResult(eq(resultCode), eq((Intent) null), resultListenerCaptor.capture());
        Onfido.OnfidoResultListener resultListener = resultListenerCaptor.getValue();
        resultListener.userExited(ExitCode.USER_LEFT_ACTIVITY);

        verify(promiseMock).reject(eq("USER_LEFT_ACTIVITY"), any(Exception.class));
    }

    @Test
    public void shouldRejectWithError() throws Exception {
        int resultCode = 123;
        onfidoSdkActivityEventListener.onActivityResult(null, checksActivityCode, resultCode, null);
        ArgumentCaptor<Onfido.OnfidoResultListener> resultListenerCaptor = ArgumentCaptor.forClass(Onfido.OnfidoResultListener.class);

        verify(onfidoClientMock).handleActivityResult(eq(resultCode), eq((Intent) null), resultListenerCaptor.capture());
        Onfido.OnfidoResultListener resultListener = resultListenerCaptor.getValue();
        resultListener.onError(new OnfidoException("example message"));

        verify(promiseMock).reject(eq("error"), any(Exception.class));
    }

    @Test
    public void shouldResolveSuccessfully() throws Exception {
        int resultCode = 123;
        onfidoSdkActivityEventListener.onActivityResult(null, checksActivityCode, resultCode, null);
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        ArgumentCaptor<Onfido.OnfidoResultListener> resultListenerCaptor = ArgumentCaptor.forClass(Onfido.OnfidoResultListener.class);
        PowerMockito.mockStatic(ReactNativeBridgeUtiles.class);
        when(ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap(any(Response.class))).thenReturn(mock(WritableMap.class));
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

        onfidoSdkActivityEventListener.onActivityResult(null, checksActivityCode, resultCode, null);
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        ArgumentCaptor<Onfido.OnfidoResultListener> resultListenerCaptor = ArgumentCaptor.forClass(Onfido.OnfidoResultListener.class);
        PowerMockito.mockStatic(ReactNativeBridgeUtiles.class);
        when(ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap(any(Response.class))).thenReturn(mock(WritableMap.class));
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
        String nfcMediaUUID = "docNfcMediaId123";
        Document documentMock = PowerMockito.mock(Document.class);
        when(documentMock.getFront()).thenReturn(new DocumentSide(docFrontId, DocSide.FRONT, DocumentType.DRIVING_LICENCE, false));
        when(documentMock.getBack()).thenReturn(new DocumentSide(docBackId, DocSide.BACK, DocumentType.DRIVING_LICENCE, false));
        when(documentMock.getNfcMediaUUID()).thenReturn(nfcMediaUUID);
        when(capturesMock.getDocument()).thenReturn(documentMock);

        onfidoSdkActivityEventListener.onActivityResult(null, checksActivityCode, resultCode, null);
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        ArgumentCaptor<Onfido.OnfidoResultListener> resultListenerCaptor = ArgumentCaptor.forClass(Onfido.OnfidoResultListener.class);
        PowerMockito.mockStatic(ReactNativeBridgeUtiles.class);
        when(ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap(any(Response.class))).thenReturn(mock(WritableMap.class));
        verify(onfidoClientMock).handleActivityResult(eq(resultCode), eq((Intent) null), resultListenerCaptor.capture());
        Onfido.OnfidoResultListener resultListener = resultListenerCaptor.getValue();

        resultListener.userCompleted(capturesMock);

        verify(promiseMock).resolve(any(WritableMap.class));
        PowerMockito.verifyStatic(ReactNativeBridgeUtiles.class);
        ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap(responseCaptor.capture());

        assertEquals(responseCaptor.getValue().document.front.id, docFrontId);
        assertEquals(responseCaptor.getValue().document.back.id, docBackId);
        assertEquals(responseCaptor.getValue().document.nfcMediaId.id, nfcMediaUUID);
        assertNull(responseCaptor.getValue().face);
    }
}