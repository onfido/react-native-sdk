#!/bin/sh

PROJ_DIR=.
MANIFEST_FILE=${PROJ_DIR}/android/src/main/AndroidManifest.xml
IOS_PLUGIN_FILE=${PROJ_DIR}/ios/PluginMetadata.m

update_manifest()
{
    sed -i -e "s/android:value=\"[0-9]*\.[0-9]*\.[0-9]*\"/android:value=\"$2\"/g" $1
}

update_plugin_file()
{
    sed -i -e "s/_pluginVersion = @\"[0-9]*\.[0-9]*\.[0-9]*\"/_pluginVersion = @\"$2\"/g" $1
}

update_manifest ${MANIFEST_FILE} $PACKAGE_VERSION
update_plugin_file ${IOS_PLUGIN_FILE} $PACKAGE_VERSION
