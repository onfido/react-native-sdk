package com.onfido.reactnative.sdk;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.onfido.android.sdk.capture.config.BiometricTokenCallback;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class BiometricTokenCallbackBridge implements BiometricTokenCallback {
    private final ReactContext reactContext;
    private Function1<? super String, Unit> provideTokenFunction;

    public BiometricTokenCallbackBridge(ReactContext reactContext) {
        this.reactContext = reactContext;
    }

    @Override
    public void onTokenGenerated(@NonNull String customerUserHash,
                                 @NonNull String biometricToken) {
        WritableMap params = Arguments.createMap();
        params.putString("customerUserHash", customerUserHash);
        params.putString("biometricToken", biometricToken);
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("onTokenGenerated", params);
    }

    @Override
    public void onTokenRequested(@NonNull String customerUserHash,
                                 @NonNull Function1<? super String, Unit> provideTokenFunction) {
        this.provideTokenFunction = provideTokenFunction;
        final WritableMap params = Arguments.createMap();
        params.putString("customerUserHash", customerUserHash);
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("onTokenRequested", params);
    }

    public void provideToken(String biometricToken) {
        if (provideTokenFunction != null) {
            provideTokenFunction.invoke(biometricToken);
        }
    }
}
