# ProGuard rules for CppDroid IDE

# Keep all Android classes
-keep class android.** { *; }
-keep interface android.** { *; }

# Keep Jetpack Compose
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Keep Kotlin
-keep class kotlin.** { *; }
-keep interface kotlin.** { *; }

# Keep Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Exceptions
-keepattributes *Annotation*,
                EnclosingMethod,
                Deprecated,
                SourceFile,
                LineNumberTable,
                *Annotation*,
                EnclosingMethod,
                SourceFile,
                LineNumberTable

# Keep OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Keep Gson
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Room
-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }

# Keep project classes
-keep class com.cppdroid.ide.** { *; }
-keep interface com.cppdroid.ide.** { *; }

# Keep model classes with reflection
-keepclassmembers class * {
    @kotlin.Metadata <fields>;
}

# Keep data classes
-keepclassmembers class * {
    public <init>(...);
}

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
}

# Optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose

# Minification
-repackageclasses
-allowaccessmodification
