# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk-windows/tools/proguard/proguard-android.txt
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
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Native
-keepclasseswithmembernames class * {
    native <methods>;
}

# Bean and Paricelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keep class com.hhly.mlottery.bean.** { *; }

# Wechat
-keep class com.hhly.mlottery.wxapi.** { *; }
# tencent
-dontwarn com.tencent.**
#-keep class com.tencent.mm.sdk.** { *; }

-dontnote org.apache.http.**
-dontwarn org.apache.http.**
-dontnote android.net.http.**
-dontwarn android.net.http.**

-keep class java.net.** { *; }

# fastjson
-dontwarn com.alibaba.fastjson.**
-dontnote com.alibaba.fastjson.**

# eventbus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

-keepclassmembers class ** {
    public void onEvent*(**);
}

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

########################## 友盟推送 Start
-dontwarn com.ut.mini.**
-dontwarn okio.**
-dontwarn com.xiaomi.**
-dontwarn com.squareup.wire.**
-dontwarn android.support.v4.**

-keepattributes *Annotation*

-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep class okio.** {*;}
-keep class com.squareup.wire.** {*;}

-keep class com.umeng.message.protobuffer.* {
    public <fields>;
    public <methods>;
}

-keep class com.umeng.message.* {
    public <fields>;
        public <methods>;
}

-keep class org.android.agoo.impl.* {
    public <fields>;
    public <methods>;
}

-keep class org.android.agoo.service.* { *; }

-keep class org.android.spdy.**{ *; }

-keep public class **.R$* {
    public static final int *;
}

-dontwarn org.apache.http.**
-dontwarn android.webkit.**
-keep class org.apache.http.** { *; }
-keep class org.apache.commons.codec.** { *; }
-keep class org.apache.commons.logging.** { *; }
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }

########################## 友盟推送 End

# License
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

-keep class org.android.spdy.** { *; }