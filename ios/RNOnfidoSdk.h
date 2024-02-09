#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED

#import <rnonfidosdk/rnonfidosdk.h>
@interface RNOnfidoSdk : RCTEventEmitter <NativeOnfidoModuleSpec>

#else

@interface RNOnfidoSdk : RCTEventEmitter <RCTBridgeModule>

#endif

@end
