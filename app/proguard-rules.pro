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
# ── CurvyKids release rules ──────────────────────────────────────────────────────
# R8 is enabled for release. The libraries below ship consumer rules that cover most
# of their surface; these add back the parts that are reached reflectively and would
# otherwise be stripped or renamed.

# Keep line numbers so Play Console crash reports stay readable, but hide the
# original source file names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ML Kit Digital Ink downloads and instantiates recognition models at runtime by name.
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_** { *; }
-dontwarn com.google.mlkit.**

# Coil selects its SVG decoder via a service-loader style lookup.
-keep class coil.decode.SvgDecoder { *; }

# Lottie parses animation JSON onto model classes reflectively.
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# Kotlin coroutines' internal service loader entries.
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
