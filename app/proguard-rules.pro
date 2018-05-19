# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Admin\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

# ----------------------------------------
# billdesk SDK
# ----------------------------------------
-keep public class com.billdesk.sdk.*
-keep public class com.billdesk.config.*
-keep public class com.billdesk.utils.URLUtilActivity
-keep public interface com.billdesk.sdk.LibraryPaymentStatusProtocol{
 public void paymentStatus(java.lang.String,android.app.Activity);
}
-keep class com.billdesk.sdk.PaymentWebView$JavaScriptInterface{
 public void gotMsg(java.lang.String);
}
-keepclassmembers class * {
 @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface
-keep public class com.billdesk.sdk.PaymentWebView$JavaScriptInterface
-keep public class * implements com.billdesk.sdk.PaymentWebView$JavaScriptInterface
-keepclassmembers class com.billdesk.sdk.PaymentWebView$JavaScriptInterface {
 <methods>;
}


# ----------------------------------------
# Picasso
# ----------------------------------------
-dontwarn com.squareup.picasso.**



# ----------------------------------------
# Glide
# ----------------------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

## for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# ----------------------------------------
# Support Library
# ----------------------------------------
-dontwarn android.support.**
-keep class android.support.** { *; }

-ignorewarnings

-keep class * {
    public private *;
}