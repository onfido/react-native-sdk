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

@end
