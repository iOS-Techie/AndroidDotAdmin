# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-renamesourcefileattribute SourceFile
-optimizationpasses 10
#-mergeinterfacesaggressively
-obfuscationdictionary method_hubz.txt
-classobfuscationdictionary classes_hubz.txt
-packageobfuscationdictionary packages_hubz.txt

#-keeppackagenames doNotKeepAThing
-flattenpackagehierarchy
-adaptclassstrings Constants,Constant
-keep class kotlin.coroutines.** { *; }
-keepattributes Signature,*Annotation*,EnclosingMethod
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF
-adaptresourcefilecontents **.properties,META-INF/AndroidManifest.xml

#EventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int e(...);

}

-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn okio.**
-keep class kotlin.jvm.** { *; }
-dontwarn kotlin.jvm.**
-keep class kotlin.coroutines.jvm.** { *; }
-dontwarn kotlin.coroutines.jvm.**

-keep class com.nyotek.dot.admin.repository.network.requests.** { *; }
-keep class com.nyotek.dot.admin.repository.network.responses.** { *; }

-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.messaging.**
-keep class io.github.g00fy2.** { *; }
-keep class com.franmontiel.localechanger.** { *;}

-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# If using AsyncExecutord, keep required constructor of default event used.
# Adjust the class name if a custom failure event type is used.
-keepclassmembers class org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Accessed via reflection, avoid renaming or removal
-keep class org.greenrobot.eventbus.android.AndroidComponentsImpl
