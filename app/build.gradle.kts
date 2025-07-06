// build.gradle (Module: app)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.echochat"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.echochat"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    viewBinding {
        enable = true
    }

    dataBinding {
        enable = true
    }

    android {
        packaging {
            resources {
                excludes.add("META-INF/INDEX.LIST")
                excludes.add("META-INF/DEPENDENCIES")
                excludes.add("META-INF/LICENSE")
                excludes.add("META-INF/LICENSE.txt")
                excludes.add("META-INF/NOTICE")
                excludes.add("META-INF/NOTICE.txt")
                excludes.add("META-INF/ASL2.0")
            }
        }
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ✅ Splash Screen
    implementation( "androidx.core:core-splashscreen:1.0.1" )

    // ✅ ZegoCloud
    implementation ("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:+")

    // ✅ ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.6.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.6.0")
    implementation("androidx.media3:media3-ui:1.6.0")

    // ✅ Firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))

    // ✅ Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // ✅ Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    // ✅ Hilt ViewModel
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // ✅ ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // ✅ Lottie Animation
    implementation("com.airbnb.android:lottie:6.1.0")

    // ✅ JSON Parsing (Gson)
    implementation("com.google.code.gson:gson:2.10.1")

    // ✅ Encryption (Tink)
    implementation("com.google.crypto.tink:tink-android:1.9.0")

    // ✅ Preferences
    implementation("androidx.preference:preference:1.2.1")

    // ✅ Logging (Timber)
    implementation("com.jakewharton.timber:timber:5.0.1")

    // ✅ Image loading (Glide)
    implementation("com.github.bumptech.glide:glide:4.12.0")

    // ✅ Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.google.android.material:material:1.11.0")

    // ✅ WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // ✅ Room database
    val roomVersion = "2.6.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // ✅ For ai gemini
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-ai")

    implementation ("com.guolindev.permissionx:permissionx:1.7.1")

}
