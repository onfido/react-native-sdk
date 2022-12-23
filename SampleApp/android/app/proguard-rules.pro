# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/Cellar/android-sdk/24.3.3/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# Needed for NFC
-keep class org.jmrtd.** { *; }
-keep class net.sf.scuba.** {*;}
-keep class org.bouncycastle.** {*;}
-keep class org.ejbca.** {*;}

# Needed for NFC
-dontwarn kotlin.time.jdk8.DurationConversionsJDK8Kt
-dontwarn org.ejbca.**
-dontwarn org.bouncycastle.**
-dontwarn module-info
-dontwarn org.jmrtd.**
-dontwarn net.sf.scuba.**
