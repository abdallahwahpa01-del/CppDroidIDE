# CppDroid IDE - C++ Game Development Editor
# ProGuard Rules for Game Development Libraries

# ============================================================================
# Keep Android Framework Classes
# ============================================================================
-keep class android.** { *; }
-keep interface android.** { *; }
-dontwarn android.**

# ============================================================================
# Keep Jetpack Compose
# ============================================================================
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }
-keep class androidx.activity.compose.** { *; }
-keep class androidx.lifecycle.compose.** { *; }

# ============================================================================
# Keep Kotlin & Coroutines
# ============================================================================
-keep class kotlin.** { *; }
-keep interface kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep interface kotlinx.coroutines.** { *; }

# ============================================================================
# Keep Game Development Libraries
# ============================================================================
# ARCore
-keep class com.google.ar.** { *; }
-keep interface com.google.ar.** { *; }
-dontwarn com.google.ar.**

# Termux (for native code execution)
-keep class com.termux.** { *; }
-keep interface com.termux.** { *; }

# Sora Editor
-keep class io.github.rosemoe.sora.** { *; }
-keep interface io.github.rosemoe.sora.** { *; }

# ============================================================================
# Keep Network Libraries
# ============================================================================
# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Exceptions,Deprecated,SourceFile,LineNumberTable

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class sun.misc.Unsafe { *; }
-dontwarn okhttp3.**
-dontwarn sun.misc.**

# Gson
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================================================
# Keep Database & Storage
# ============================================================================
# Room
-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }
-keep @interface androidx.room.**

# DataStore
-keep class androidx.datastore.** { *; }
-keep interface androidx.datastore.** { *; }

# ============================================================================
# Keep Project Classes
# ============================================================================
-keep class com.cppdroid.ide.** { *; }
-keep interface com.cppdroid.ide.** { *; }
-keepclassmembers class com.cppdroid.ide.** {
    public <init>(...);
    public <fields>;
    public <methods>;
}

# ============================================================================
# Keep Data Classes & Models
# ============================================================================
-keepclassmembers class * {
    @kotlin.Metadata <fields>;
}

-keep class * {
    @com.google.gson.annotations.Expose <fields>;
}

# ============================================================================
# Keep Native Code Access
# ============================================================================
-keepclasseswithmembernames class * {
    native <methods>;
}

# ============================================================================
# Remove Logging (for release builds)
# ============================================================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ============================================================================
# Optimization & Minification
# ============================================================================
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
-repackageclasses
-allowaccessmodification

# ============================================================================
# Keep Enum Classes (important for game development)
# ============================================================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================================================
# Keep Serialization Classes
# ============================================================================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================================================
# Keep Animation & Graphics
# ============================================================================
-keep class android.graphics.** { *; }
-keep class android.opengl.** { *; }
-keep interface android.opengl.** { *; }
-keep class android.animation.** { *; }

# ============================================================================
# Keep Configuration Classes
# ============================================================================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
