package com.onfido.reactnative.sdk;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;

import com.onfido.android.sdk.capture.ExitCode;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.upload.Captures;
import com.onfido.android.sdk.capture.errors.OnfidoException;
import com.onfido.workflow.OnfidoWorkflow;

class OnfidoSdkActivityEventListener extends BaseActivityEventListener {

    /* package */ final Onfido client;
    private Promise currentPromise = null;

    static final int workflowActivityCode = 102030;
    static final int checksActivityCode = 102040;


    public OnfidoSdkActivityEventListener(final Onfido client) {
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
        super.onActivityResult(activity, requestCode, resultCode, data);

        if (requestCode == workflowActivityCode) {
            handleOnfidoWorkflow(OnfidoWorkflow.create(activity), resultCode, data);
        }

        if (requestCode == checksActivityCode) {
            handleOnfidoChecks(resultCode, data);
        }
    }

    private void handleOnfidoWorkflow(OnfidoWorkflow workflow, int resultCode, Intent data) {
        workflow.handleActivityResult(resultCode, data, new OnfidoWorkflow.ResultListener() {
            @Override
            public void onUserCompleted() {
                if (currentPromise != null) {
                    currentPromise.resolve("");
                }
            }

            @Override
            public void onUserExited(@NonNull ExitCode exitCode) {
                if (currentPromise != null) {
                    currentPromise.reject(exitCode.toString(), new Exception("User exited by manual action."));
                    currentPromise = null;
                }
            }

            @Override
            public void onException(@NonNull OnfidoWorkflow.WorkflowException e) {
                if (currentPromise != null) {
                    currentPromise.reject("error", e);
                    currentPromise = null;
                }
            }
        });
    }

    private void handleOnfidoChecks(int resultCode, Intent data) {
        client.handleActivityResult(resultCode, data, new Onfido.OnfidoResultListener() {
            @Override
            public void userCompleted(@NonNull Captures captures) {
                if (currentPromise != null) {
                    String docFrontId = null;
                    String docBackId = null;
                    String nfcMediaUUID = null;
                    String faceId = null;
                    String faceVarient = null;
                    if (captures.getDocument() != null) {
                        if (captures.getDocument().getFront() != null) {
                            docFrontId = captures.getDocument().getFront().getId();
                        }
                        if (captures.getDocument().getBack() != null) {
                            docBackId = captures.getDocument().getBack().getId();
                        }
                        if (captures.getDocument().getNfcMediaUUID() != null) {
                            nfcMediaUUID = captures.getDocument().getNfcMediaUUID();
                        }
                    }
                    if (captures.getFace() != null) {
                        faceId = captures.getFace().getId();
                        captures.getFace().getVariant();
                        faceVarient = captures.getFace().getVariant().toString();
                    }

                    final Response response = new Response(docFrontId, docBackId, faceId, faceVarient, nfcMediaUUID);
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
            public void userExited(@NonNull final ExitCode exitCode) {
                if (currentPromise != null) {
                    currentPromise.reject(exitCode.toString(), new Exception("User exited by manual action."));
                    currentPromise = null;
                }
            }

            @Override
            public void onError(@NonNull final OnfidoException e) {
                if (currentPromise != null) {
                    currentPromise.reject("error", e);
                    currentPromise = null;
                }
            }
        });
    }
};
