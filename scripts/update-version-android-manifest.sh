#!/bin/sh

PROJ_DIR=.
MANIFEST_FILE=${PROJ_DIR}/android/src/main/AndroidManifest.xml

update_manifest()
{
    sed -i -e "s/android:value=\"[0-9]*\.[0-9]*\.[0-9]*\"/android:value=\"$2\"/g" $1
}

update_manifest ${MANIFEST_FILE} $PACKAGE_VERSION
