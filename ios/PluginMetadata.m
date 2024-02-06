#import "PluginMetadata.h"

@implementation PluginMetadata

- (instancetype)init
{
    self = [super init];
    if (self) {
        _pluginPlatform = @"react-native";
        _pluginVersion = @"10.7.0";
    }
    return self;
}

@end
