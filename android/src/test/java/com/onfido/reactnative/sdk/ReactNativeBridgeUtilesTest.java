package com.onfido.reactnative.sdk;

import static org.junit.Assert.assertEquals;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.WritableMap;
import com.onfido.android.sdk.capture.config.DocumentMetadata;
import com.onfido.android.sdk.capture.config.MediaFile;
import com.onfido.android.sdk.capture.config.MediaResult;
import com.onfido.android.sdk.capture.config.MediaResult.DocumentResult;
import com.onfido.android.sdk.capture.config.MediaResult.LivenessResult;
import com.onfido.android.sdk.capture.config.MediaResult.SelfieResult;
import com.onfido.android.sdk.capture.utils.CountryCode;
import com.onfido.api.client.data.DocSide;
import com.onfido.api.client.data.DocType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Arguments.class)
public class ReactNativeBridgeUtilesTest {

    @Before
    public void init() {
        // Facebook's WritableMap uses platform-specific implementations, so it must be mocked
        // See http://g.co/androidstudio/not-mocked
        PowerMockito.mockStatic(Arguments.class);
        Answer<Object> answer = invocation -> new JavaOnlyMap();
        PowerMockito.when(Arguments.createMap()).thenAnswer(answer);
    }

    @Test
    public void getMediaResultMap_should_build_correct_map_for_selfie_mediaResult() {

        MediaFile mediaFile = new MediaFile(new byte[20], "jpeg", "selfie_title");
        MediaResult testMediaResult = new SelfieResult(mediaFile);

        WritableMap expected = Arguments.createMap();
        expected.putString(ReactNativeBridgeUtiles.KEY_CAPTURE_TYPE, "FACE");
        expected.putString(ReactNativeBridgeUtiles.KEY_FILE_DATA, Arrays.toString(mediaFile.getFileData()));
        expected.putString(ReactNativeBridgeUtiles.KEY_FILE_TYPE, "jpeg");
        expected.putString(ReactNativeBridgeUtiles.KEY_FILE_NAME, "selfie_title");

        WritableMap result = ReactNativeBridgeUtiles.getMediaResultMap(testMediaResult);

        assertEquals(4, result.toHashMap().keySet().size());
        assertEquals(expected, result);
    }

    @Test
    public void getMediaResultMap_should_build_correct_map_for_liveness_mediaResult() {

        MediaFile mediaFile = new MediaFile(new byte[20], "mp4", "video_title");
        MediaResult testMediaResult = new LivenessResult(mediaFile);

        WritableMap expected = Arguments.createMap();
        expected.putString(ReactNativeBridgeUtiles.KEY_CAPTURE_TYPE, "VIDEO");
        expected.putString(ReactNativeBridgeUtiles.KEY_FILE_DATA, Arrays.toString(mediaFile.getFileData()));
        expected.putString(ReactNativeBridgeUtiles.KEY_FILE_TYPE, "mp4");
        expected.putString(ReactNativeBridgeUtiles.KEY_FILE_NAME, "video_title");

        WritableMap result = ReactNativeBridgeUtiles.getMediaResultMap(testMediaResult);

        assertEquals(4, result.toHashMap().keySet().size());
        assertEquals(expected, result);
    }

    @Test
    public void getMediaResultMap_should_build_correct_map_for_document_mediaResult() {

        MediaFile mediaFile = new MediaFile(new byte[20], "jpeg", "document_title");
        DocumentMetadata metadata = new DocumentMetadata(
                DocSide.FRONT.name(), DocType.PASSPORT.name(), CountryCode.US.name()
        );
        MediaResult testMediaResult = new DocumentResult(mediaFile, metadata);

        WritableMap expected = Arguments.createMap();
        expected.putString(ReactNativeBridgeUtiles.KEY_CAPTURE_TYPE, "DOCUMENT");
        expected.putString(ReactNativeBridgeUtiles.KEY_FILE_DATA, Arrays.toString(mediaFile.getFileData()));
        expected.putString(ReactNativeBridgeUtiles.KEY_FILE_TYPE, "jpeg");
        expected.putString(ReactNativeBridgeUtiles.KEY_FILE_NAME, "document_title");
        //Metadata
        expected.putString(ReactNativeBridgeUtiles.KEY_DOCUMENT_SIDE, "FRONT");
        expected.putString(ReactNativeBridgeUtiles.KEY_DOCUMENT_TYPE, "PASSPORT");
        expected.putString(ReactNativeBridgeUtiles.KEY_DOCUMENT_ISSUING_COUNTRY, "US");

        WritableMap result = ReactNativeBridgeUtiles.getMediaResultMap(testMediaResult);

        assertEquals(7, result.toHashMap().keySet().size());
        assertEquals(expected, result);
    }
}