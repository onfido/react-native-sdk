-keep class com.onfido.reactnative.sdk.** { *; }

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
