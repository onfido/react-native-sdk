package com.onfido.reactnative.sdk;

import android.app.Activity;
import android.content.Intent;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;

import com.onfido.android.sdk.capture.ExitCode;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.upload.Captures;
import com.onfido.android.sdk.capture.errors.OnfidoException;

class OnfidoSdkActivityEventListener extends BaseActivityEventListener {

    /* package */ final Onfido client;
    private Promise currentPromise = null;

    public OnfidoSdkActivityEventListener(final Onfido client){
        this.client = client;
    }

    /**
     * Sets the current promise to be resolved.
     * 
     * @param currentPromise the promise to set
     */
    public void setCurrentPromise(Promise currentPromise) {
        this.currentPromise = currentPromise;
    }

    @Override
    public void onActivityResult(final Activity activity, final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        client.handleActivityResult(resultCode, data, new Onfido.OnfidoResultListener() {
            @Override
            public void userCompleted(Captures captures) {
                if (currentPromise != null) {
                    String docFrontId = null;
                    String docBackId = null;
                    String faceId = null;
                    String faceVarient = null;
                    if (captures.getDocument() != null) {
                        if (captures.getDocument().getFront() != null) {
                            docFrontId = captures.getDocument().getFront().getId();
                        }
                        if (captures.getDocument().getBack() != null) {
                            docBackId = captures.getDocument().getBack().getId();
                        }
                    }
                    if (captures.getFace() != null) {
                        faceId = captures.getFace().getId();
                        if (captures.getFace().getVariant() != null) {
                            faceVarient = captures.getFace().getVariant().toString();
                        }
                    }

                    final Response response = new Response(docFrontId, docBackId, faceId, faceVarient);
                    try {
                       final WritableMap responseMap = ReactNativeBridgeUtiles.convertPublicFieldsToWritableMap(response);
                       currentPromise.resolve(responseMap);
                        currentPromise = null;
                    } catch (Exception e) {
                        currentPromise.reject("error", "Error serializing response", e);
                        currentPromise = null;
                    }
                }
            }

            @Override
            public void userExited(final ExitCode exitCode) {
                if (currentPromise != null) {
                    currentPromise.reject("cancel", new Exception("User exited by clicking the back button."));
                    currentPromise = null;
                }
            }

            @Override
            public void onError(final OnfidoException e) {
                if (currentPromise != null) {
                    currentPromise.reject("error", e);
                    currentPromise = null;
                }
            }
        });
    }
};