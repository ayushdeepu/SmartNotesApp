# Add project specific ProGuard rules here.
# By default, the flags in proguard-android-optimize.txt are used.
# Minification is disabled in this project, so no extra rules are required.

# Keep Room generated classes safe if minification is ever enabled.
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
