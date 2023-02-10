#import "RNOnfidoSdk.h"
#import <onfido_react_native_sdk-Swift.h>

@implementation RNOnfidoSdk {
    OnfidoSdk *_onfidoSdk;
}

RCT_EXPORT_MODULE()

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (instancetype)init
{
    if (!(self = [super init])) {
        return nil;
    }
    
    _onfidoSdk = [OnfidoSdk alloc];
    
    return self;
}

RCT_EXPORT_METHOD(start:(NSDictionary *)config resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
    [_onfidoSdk start:config resolver:resolve rejecter:reject];
}

#ifdef RCT_NEW_ARCH_ENABLED

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
    return std::make_shared<facebook::react::NativeOnfidoModuleSpecJSI>(params);
}

#endif

@end
