# ProGuard/R8 rules for Multiply app
# Build uses: Jetpack Compose, Hilt (Dagger), Kotlin coroutines, DataStore Preferences,
# Navigation-Compose, and kotlinx.serialization (plugin applied, conservative keep below).
# Release build is minified and shrinks resources.

# ----- General Kotlin/Android keeps -----
# Keep important class attributes used by Kotlin, generics, and DI generated code
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature

# Keep enum valueOf()/values() methods (defensive; sometimes used reflectively)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ----- Android components -----
# These are kept automatically by default rules via manifest, but we keep names defensively
-keep class com.stephenwanjala.multiply.MultiplyApp
-keep class com.stephenwanjala.multiply.MainActivity

# ----- Dagger/Hilt -----
# Hilt provides its own keep rules; suppress possible warnings from generated/internal types
-dontwarn dagger.hilt.internal.**
-dontwarn dagger.internal.**
-dontwarn javax.inject.**

# ----- Kotlinx Serialization (scoped to Navigation destinations) -----
# We only serialize navigation destinations; keep just those to avoid broad keeps
-keep class com.stephenwanjala.multiply.ui.navigation.MultiplyDestination
-keep class com.stephenwanjala.multiply.ui.navigation.MultiplyDestination$* { *; }
-dontwarn kotlinx.serialization.**

# ----- Jetpack Compose -----
# Compose libraries ship consumer rules; no extra keeps are typically required.
# Suppress potential warnings from generated/inlined code in Compose tooling
-dontwarn androidx.compose.**

## ----- Coroutines / Kotlin stdlib -----
## These are well-supported by R8; silence potential harmless warnings
-dontwarn kotlinx.coroutines.**
#
## ----- DataStore Preferences -----
-dontwarn androidx.datastore.**

# ----- Optional: keep line numbers for better crash reports (uncomment if desired) -----
# -keepattributes SourceFile,LineNumberTable
# -renamesourcefileattribute SourceFile