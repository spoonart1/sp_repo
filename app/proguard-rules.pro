# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-renamesourcefileattribute ''
-dontwarn android.support.v7.***
-dontwarn android.support.v4.***
-keep class android.support.v4.*** { *; }
-keep interface android.support.v4.*** { *; }
-keep class android.support.v7.*** { *; }
-keep interface android.support.v7.*** { *; }
-dontshrink
-dontoptimize
-dontskipnonpubliclibraryclasses
#verbose dihidupkan jika sedang debug
#-verbose
-dontusemixedcaseclassnames
-keepattributes Signature
-keepattributes *Annotation*,SourceFile,LineNumberTable

-keep class javax.*** { *; }
-keep interface javax.*** { *; }
-dontwarn javax.***

-keep class java.*** { *; }
-keep interface java.*** { *; }
-dontwarn java.***

-keep class org.*** { *; }
-keep interface org.*** { *; }
-dontwarn org.***

-keep class com.*** { *; }
-keep interface com.*** { *; }
-dontwarn com.***

-keep class retrofit.*** { *; }
-keep interface retrofit.*** { *; }
-dontwarn retrofit.***

-keep class android.*** { *; }
-keep interface android.*** { *; }
-dontwarn android.***

-keep class javasign.com.dompetsehat.view.*** { *; }
-keep interface javasign.com.dompetsehat.view.*** { *; }
-dontwarn javasign.com.dompetsehat.view.***

-keep class javasign.com.dompetsehat.models.*** { *; }
-keep interface javasign.com.dompetsehat.models.*** { *; }
-dontwarn javasign.com.dompetsehat.models.***

#Keep Library Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepnames class * { @butterknife.Bind *;}

-keep class android.widget.*** { *; }
-dontwarn android.widget.***

-keep class .R
-keep class **.R$* {
    <fields>;
}

-dontwarn io.rx_cache.internal.**
-dontwarn rx.internal.**
-keepclassmembers enum * { *; }

-keep class com.google.gson.*** { *; }
-keep interface com.google.gson.*** { *; }
-dontwarn com.google.gson.***

-keep class rx.*** { *; }
-keep interface rx.*** { *; }
-dontwarn rx.***

-keep class timber.log.*** { *; }
-keep interface timber.log.*** { *; }
-dontwarn timber.log.***

-keep class io.smooch.core.*** { *; }
-keep interface io.smooch.core.*** { *; }
-dontwarn io.smooch.core.***

-keep class com.facebook.*** { *; }
-keep interface com.facebook.*** { *; }
-dontwarn com.facebook.***

-keep class com.crashlytics.android.core.*** { *; }
-keep interface com.crashlytics.android.core.*** { *; }
-dontwarn com.crashlytics.android.core.***

-keep class javasign.com.dompetsehat.utils.SessionManager { *; }

-dontwarn com.evernote.android.job.gcm.**
-dontwarn com.evernote.android.job.util.GcmAvailableHelper

-keep public class com.evernote.android.job.v21.PlatformJobService
-keep public class com.evernote.android.job.v14.PlatformAlarmService
-keep public class com.evernote.android.job.v14.PlatformAlarmReceiver
-keep public class com.evernote.android.job.JobBootReceiver



#-keep class javasign.com.dompetsehat.ui.*** { *; }
#-keep interface javasign.com.dompetsehat.ui.*** { *; }
#-dontwarn javasign.com.dompetsehat.ui.***



