package com.onfido.reactnative.sdk;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.onfido.android.sdk.capture.analytics.OnfidoAnalyticsEvent;
import com.onfido.android.sdk.capture.analytics.OnfidoAnalyticsEventListener;

public class AnalyticsCallbackBridge implements OnfidoAnalyticsEventListener {

    public static final String CALLBACK_NAME = "onfidoAnalyticsCallback";
    private final ReactApplicationContext reactContext;

    public AnalyticsCallbackBridge(ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
    }

    @Override
    public void onEvent(@NonNull OnfidoAnalyticsEvent event) {

        var properties = Arguments.createMap();
        for (var entry : event.getProperties().entrySet()) {
            properties.putString(entry.getKey().toString(), entry.getValue());
        }

        var params = Arguments.createMap();
        params.putString("type", event.getType().toString());
        params.putMap("properties", properties);

        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(CALLBACK_NAME, params);
    }
}
