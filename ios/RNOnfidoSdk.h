#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

#ifdef RCT_NEW_ARCH_ENABLED

#import <rnonfidosdk/rnonfidosdk.h>
@interface RNOnfidoSdk : NSObject <NativeOnfidoModuleSpec>

#else

@interface RNOnfidoSdk : NSObject <RCTBridgeModule>

#endif

@end
