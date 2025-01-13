//
//  OnfidoSdk.m
//
//  Copyright © 2016-2025 Onfido. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
@interface RCT_EXTERN_MODULE(OnfidoSdk, RCTEventEmitter)

RCT_EXTERN_METHOD(
    start:(NSDictionary *)config
    resolver:(RCTPromiseResolveBlock)resolve
    rejecter:(RCTPromiseRejectBlock)reject
)
RCT_EXTERN_METHOD(supportedEvents)
RCT_EXTERN_METHOD(withMediaCallbacksEnabled)
RCT_EXTERN_METHOD(withBiometricTokenCallback)
RCT_EXTERN_METHOD(provideBiometricToken:(NSString *)biometricToken)

@end
