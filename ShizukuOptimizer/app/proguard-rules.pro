# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Shizuku classes
-keep class rikka.shizuku.** { *; }
-keep class moe.shizuku.** { *; }

# Keep model classes
-keep class com.optimizer.shizuku.utils.** { *; }
