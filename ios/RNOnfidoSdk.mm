#import "RNOnfidoSdk.h"
#import <onfido_react_native_sdk-Swift.h>

@implementation RNOnfidoSdk {
    OnfidoSdk *_onfidoSdk;
}

RCT_EXPORT_MODULE()

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

- (instancetype)init
{
    if (!(self = [super init])) {
        return nil;
    }
    
    _onfidoSdk = [OnfidoSdk alloc];
    
    // capture weak self reference to prevent retain cycle
    __weak __typeof__(self) weakSelf = self;
    
    _onfidoSdk.mediaCallbackHandler = ^(NSDictionary *data) {
        __typeof__(self) strongSelf = weakSelf;
        
        if (strongSelf != nullptr) {
            [strongSelf sendEventWithName:@"onfidoMediaCallback" body:data];
        }
    };
    
    return self;
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"onfidoMediaCallback"];
}

RCT_EXPORT_METHOD(start:(NSDictionary *)config resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
    [_onfidoSdk start:config resolver:resolve rejecter:reject];
}

RCT_EXPORT_METHOD(withMediaCallbacksEnabled)
{
    [_onfidoSdk withMediaCallbacksEnabled];
}

#ifdef RCT_NEW_ARCH_ENABLED

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
    return std::make_shared<facebook::react::NativeOnfidoModuleSpecJSI>(params);
}

#endif

@end
