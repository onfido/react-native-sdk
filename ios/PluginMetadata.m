//
//  PluginMetadata.m
//
//  Copyright © 2016-2024 Onfido. All rights reserved.
//

#import "PluginMetadata.h"

@implementation PluginMetadata

- (instancetype)init
{
    self = [super init];
    if (self) {
        _pluginPlatform = @"react-native";
        _pluginVersion = @"12.2.0";
    }
    return self;
}

@end
