# ProGuard/R8 rules for Multiply app - Maximum APK size reduction
# CRITICAL: Also add to gradle.properties:

# ----- Extreme optimization settings -----
-allowaccessmodification
-repackageclasses ''
-overloadaggressively
-mergeinterfacesaggressively

# ----- Minimal attribute preservation -----
-keepattributes Signature,RuntimeVisible*Annotations
# Uncomment for crash reports (adds ~1-2KB):
# -keepattributes SourceFile,LineNumberTable
# -renamesourcefileattribute SourceFile

# ----- Enum optimization -----
# Only keep enum methods if actually used reflectively
-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ----- Android components (allow obfuscation) -----
-keep,allowobfuscation,allowshrinking class com.stephenwanjala.multiply.MultiplyApp
-keep,allowobfuscation,allowshrinking class com.stephenwanjala.multiply.MainActivity

# ----- Dagger/Hilt -----
# Hilt's consumer rules handle most; suppress warnings to avoid bloat from defensive keeps
-dontwarn dagger.**
-dontwarn javax.inject.**

# ----- Kotlinx Serialization (Navigation only) -----
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <1>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.stephenwanjala.multiply.ui.navigation.**$serializer { *; }
-keepclassmembers class com.stephenwanjala.multiply.ui.navigation.** {
    *** Companion;
}
-keepclasseswithmembers class com.stephenwanjala.multiply.ui.navigation.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ----- Jetpack Compose -----
# Compose libraries have consumer rules; suppress warnings
-dontwarn androidx.compose.**

# ----- Coroutines / Kotlin stdlib -----
-dontwarn kotlinx.coroutines.**
-dontwarn kotlin.**

# ----- DataStore Preferences -----
-dontwarn androidx.datastore.**

# ----- Remove logging in release -----
# Strip all Log calls to reduce code size (optional but recommended)
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# ----- Remove runtime null checks (Kotlin) -----
# R8 can remove Kotlin's intrinsic null checks in release builds
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkNotNull(...);
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkExpressionValueIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkReturnedValueIsNotNull(...);
    public static void checkFieldIsNotNull(...);
}