# This is a configuration file for ProGuard.

-keep class com.receiptmerger.app.** { *; }
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom application classes
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
