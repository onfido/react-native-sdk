//
//  PluginMetadata.m
//
//  Copyright © 2016-2025 Onfido. All rights reserved.
//

#import "PluginMetadata.h"

@implementation PluginMetadata

- (instancetype)init
{
    self = [super init];
    if (self) {
        _pluginPlatform = @"react-native";
        _pluginVersion = @"16.1.0";
    }
    return self;
}

@end
