# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\codeProjects\libs\AndroidSdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class com.vitech.socmcompetition.StorySubmitActivity$StoryGrabberInterface{
   public *;
}
-keepattributes StoryGrabberInterface

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.instamojo.android.**{*;}
-keeppackagenames org.jsoup.nodes
-keep class com.squareup.okhttp.*{ *; }
-dontwarn com.squareup.okhttp.**
-keep class java.nio.file.*{ *; }
-dontwarn java.nio.file.**
-keep class org.codehaus.mojo.animal_sniffer.*{ *; }
-dontwarn org.codehaus.mojo.animal_sniffer.**
-keep class org.spongycastle.** { *; }
-dontwarn org.spongycastle.**
-keep class javax.xml.crypto.**{*;}
-dontwarn javax.xml.crypto.**
-keep class org.apache.**{*;}
-dontwarn org.apache.**