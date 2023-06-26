package com.onfido.reactnative.sdk;

import static com.onfido.reactnative.sdk.ReactNativeBridgeUtiles.KEY_CAPTURE_TYPE;
import static com.onfido.reactnative.sdk.ReactNativeBridgeUtiles.KEY_COUNT;
import static com.onfido.reactnative.sdk.ReactNativeBridgeUtiles.KEY_DATA;
import static com.onfido.reactnative.sdk.ReactNativeBridgeUtiles.KEY_DOCUMENT_ISSUING_COUNTRY;
import static com.onfido.reactnative.sdk.ReactNativeBridgeUtiles.KEY_DOCUMENT_SIDE;
import static com.onfido.reactnative.sdk.ReactNativeBridgeUtiles.KEY_DOCUMENT_TYPE;
import static com.onfido.reactnative.sdk.ReactNativeBridgeUtiles.KEY_FILE_DATA;
import static com.onfido.reactnative.sdk.ReactNativeBridgeUtiles.KEY_FILE_NAME;
import static com.onfido.reactnative.sdk.ReactNativeBridgeUtiles.KEY_FILE_TYPE;
import static com.onfido.reactnative.sdk.ReactNativeBridgeUtiles.KEY_INDEX;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ResultReceiver;

import androidx.annotation.NonNull;

import com.onfido.android.sdk.capture.config.DocumentMetadata;
import com.onfido.android.sdk.capture.config.MediaCallback;
import com.onfido.android.sdk.capture.config.MediaFile;
import com.onfido.android.sdk.capture.config.MediaResult;
import com.onfido.android.sdk.capture.config.MediaResult.DocumentResult;
import com.onfido.android.sdk.capture.config.MediaResult.LivenessResult;
import com.onfido.android.sdk.capture.config.MediaResult.SelfieResult;
import com.onfido.android.sdk.capture.ui.CaptureType;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CallbackResultReceiver implements MediaCallback {

    private static final int MAX_CHUNKED_MEDIA_SIZE_IN_BYTE = 256 * 1024;
    private static final Long INTERVAL_BETWEEN_RESULT_RECEIVER_MS = 10L;

    ResultReceiver receiver;

    public CallbackResultReceiver(ResultReceiver receiver) {
        this.receiver = receiver;
    }

    protected CallbackResultReceiver(Parcel in) {
        this.receiver = in.readParcelable(ResultReceiver.class.getClassLoader());
    }

    @Override
    public void onMediaCaptured(@NonNull Context context, @NonNull MediaResult mediaResult) {
        Bundle dataBundle = new Bundle();

        if (mediaResult instanceof DocumentResult) {
            DocumentResult documentResult = (DocumentResult) mediaResult;
            dataBundle.putSerializable(KEY_CAPTURE_TYPE, CaptureType.DOCUMENT);

            DocumentMetadata metadata = documentResult.getDocumentMetadata();
            dataBundle.putString(KEY_DOCUMENT_TYPE, metadata.getType());
            dataBundle.putString(KEY_DOCUMENT_SIDE, metadata.getSide());
            dataBundle.putString(KEY_DOCUMENT_ISSUING_COUNTRY, metadata.getIssuingCountry());

            sendMedia(dataBundle, documentResult.getFileData());

        } else if (mediaResult instanceof LivenessResult) {
            dataBundle.putSerializable(KEY_CAPTURE_TYPE, CaptureType.VIDEO);
            sendMedia(dataBundle, ((LivenessResult) mediaResult).getFileData());

        } else if (mediaResult instanceof SelfieResult) {
            dataBundle.putSerializable(KEY_CAPTURE_TYPE, CaptureType.FACE);
            sendMedia(dataBundle, ((SelfieResult) mediaResult).getFileData());
        }
    }

    public Disposable sendMedia(Bundle dataBundle, MediaFile mediaFile) {

        byte[] documentData = mediaFile.getFileData();

        ArrayList<byte[]> chunkedData = chunked(MAX_CHUNKED_MEDIA_SIZE_IN_BYTE, documentData);
        ArrayList<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < chunkedData.size(); i++) {
            pairs.add(new Pair(i, chunkedData.get(i)));
        }

        Scheduler scheduler = Schedulers.io();
        return Observable.zip(
                Observable.fromIterable(pairs)
                        .observeOn(scheduler)
                        .subscribeOn(scheduler),
                Observable.interval(INTERVAL_BETWEEN_RESULT_RECEIVER_MS, TimeUnit.MILLISECONDS, scheduler),
                (bytes, aLong) -> bytes
        ).subscribe(pair -> {
                    Bundle fileBundle = new Bundle();
                    fileBundle.putByteArray(KEY_DATA, pair.data);
                    fileBundle.putInt(KEY_INDEX, pair.index);
                    fileBundle.putInt(KEY_COUNT, chunkedData.size());

                    dataBundle.putBundle(KEY_FILE_DATA, fileBundle);
                    dataBundle.putString(KEY_FILE_NAME, mediaFile.getFileName());
                    dataBundle.putString(KEY_FILE_TYPE, mediaFile.getFileType());

                    receiver.send(Activity.RESULT_OK, dataBundle);
                }
        );
    }

    public ArrayList<byte[]> chunked(int size, byte[] array) {
        ArrayList<byte[]> listOfChunkedByteArrays = new ArrayList<>();
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);

        while (byteBuffer.hasRemaining()) {
            byte[] chunk = new byte[Math.min(size, byteBuffer.remaining())];
            byteBuffer.get(chunk, 0, chunk.length);
            listOfChunkedByteArrays.add(chunk);
        }
        return listOfChunkedByteArrays;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.receiver, flags);
    }

    public static final Creator<CallbackResultReceiver> CREATOR = new Creator<CallbackResultReceiver>() {
        @Override
        public CallbackResultReceiver createFromParcel(Parcel source) {
            return new CallbackResultReceiver(source);
        }

        @Override
        public CallbackResultReceiver[] newArray(int size) {
            return new CallbackResultReceiver[size];
        }
    };

    public static ResultReceiver receiverForSending(ResultReceiver actualReceiver) {
        Parcel parcel = Parcel.obtain();
        actualReceiver.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        ResultReceiver receiverForSending = ResultReceiver.CREATOR.createFromParcel(parcel);

        parcel.recycle();
        return receiverForSending;
    }

}
