package com.onfido.reactnative.sdk;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facebook.react.TurboReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.module.annotations.ReactModuleList;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import com.facebook.react.uimanager.ViewManager;

@ReactModuleList(
        nativeModules = {
                OnfidoSdkModule.class
        }
)
public class OnfidoSdkPackage extends TurboReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(new OnfidoSdkModule(reactContext));
    }

    @Nullable
    @Override
    public NativeModule getModule(String name, ReactApplicationContext reactApplicationContext) {
        switch (name) {
            case OnfidoSdkModule.NAME:
                return new OnfidoSdkModule(reactApplicationContext);
            default:
                return null;
        }
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public ReactModuleInfoProvider getReactModuleInfoProvider() {
        try {
            Class<?> reactModuleInfoProviderClass =
                    Class.forName("com.onfido.reactnative.sdk.OnfidoSdkPackage$$ReactModuleInfoProvider");
            return (ReactModuleInfoProvider) reactModuleInfoProviderClass.newInstance();
        } catch (ClassNotFoundException e) {
            // ReactModuleSpecProcessor does not run at build-time. Create this ReactModuleInfoProvider by
            // hand.
            return new ReactModuleInfoProvider() {
                @Override
                public Map<String, ReactModuleInfo> getReactModuleInfos() {
                    final Map<String, ReactModuleInfo> reactModuleInfoMap = new HashMap<>();

                    Class<? extends NativeModule>[] moduleList =
                            new Class[] {
                                    OnfidoSdkModule.class,
                            };

                    for (Class<? extends NativeModule> moduleClass : moduleList) {
                        ReactModule reactModule = moduleClass.getAnnotation(ReactModule.class);

                        reactModuleInfoMap.put(
                                reactModule.name(),
                                new ReactModuleInfo(
                                        reactModule.name(),
                                        moduleClass.getName(),
                                        reactModule.canOverrideExistingModule(),
                                        reactModule.needsEagerInit(),
                                        reactModule.hasConstants(),
                                        reactModule.isCxxModule(),
                                        TurboModule.class.isAssignableFrom(moduleClass)));
                    }

                    return reactModuleInfoMap;
                }
            };
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(
                    "No ReactModuleInfoProvider for com.onfido.reactnative.sdk.OnfidoSdkPackage$$ReactModuleInfoProvider", e);
        }
    }
}
