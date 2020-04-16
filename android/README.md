README
======

If you want to publish the lib as a maven dependency, follow these steps before publishing a new version to npm:

1. Be sure to have the Android [SDK](https://developer.android.com/studio/index.html) and [NDK](https://developer.android.com/ndk/guides/index.html) installed
2. Be sure to have a `local.properties` file in this folder that points to the Android SDK and NDK
```
ndk.dir=/Users/{username}/Library/Android/sdk/ndk-bundle
sdk.dir=/Users/{username}/Library/Android/sdk
```
3. Delete the `maven` folder
4. Run `./gradlew installArchives`
5. Verify that latest set of generated files is in the maven folder with the correct version number


How to quickly develop the Android Java code using the TestApp:
======
React Native's "Fast Refresh" feature will not update Java code as you make changes, and reinstalling all npm packages is slow.  Instead, you can follow this process to recompile only the Java code when you make changes.

In one console, from the `TestApp/` directory, run the following commands.  It may take 2 or more minutes to build.  You only need to run this once: leave it running in the background while you develop.
```shell
rm -rf node_modules/ && yarn && cd .. && watchman watch-del-all && npx react-native start --reset-cache
```

In a second console, from the `TestApp/` directory, update the Android package and launch the virtual device.  Run this each time you change Android code.
```shell
rsync -av ../ node_modules/@onfido/react-native-sdk/ --exclude=TestApp --exclude=SampleApp --exclude=node_modules --exclude=android/build --exclude=.git && npx react-native run-android
```

How to run the tests
======
1. Run "yarn" or "npm install" from the project root.  This will download the React Native Facebook bridge library
2. Run "./gradlew test" from the "/android" directory.
