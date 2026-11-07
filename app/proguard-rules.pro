# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve JavascriptInterface methods for WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserve WebView classes
-keepclassmembers class * extends android.webkit.WebViewClient {
    public *;
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
    public *;
}

# Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**

# Preserve Kotlin Reflection / Serialization if used
-keepattributes SourceFile,LineNumberTable
