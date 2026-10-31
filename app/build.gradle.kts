plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

/** Voice packs delivered on demand by Play; see audio/packs/VoicePack.kt. */
val voicePacks = listOf(":voice_hi_letters", ":voice_hi_barakhadi")

/**
 * Plain debug APKs can't receive Play asset packs, so by default debug builds carry the packs'
 * audio inside the app and the clips play straight away. Build with -Pvoice.bundled=false (and
 * bundletool --local-testing) to try the real download flow.
 */
val bundleVoiceInDebug = (findProperty("voice.bundled") as String?)?.toBoolean() ?: true

android {
    namespace = "com.vi5hnu.curvykids"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vi5hnu.curvykids"
        minSdk = 29
        targetSdk = 36
        versionCode = 4
        versionName = "2.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // R8 shrinking + resource shrinking. The app ships a 4.7 MB hand-drawn SVG asset
            // set, so keeping the code and unused resources trimmed matters for install size.
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        compose = true
    }

    assetPacks += voicePacks
    if (bundleVoiceInDebug) {
        sourceSets.getByName("debug").assets.srcDirs(
            voicePacks.map { "../${it.removePrefix(":")}/src/main/assets" }
        )
    }

    packaging {
        jniLibs {
            // Store .so files uncompressed so the OS can memory-map them at 16 KB page
            // boundaries — required for Android 15+ 16 KB page-size support (mandatory at API 36).
            useLegacyPackaging = false
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
//
    //google ink recognization
    implementation(libs.digital.ink.recognition)

    implementation(libs.kotlinx.coroutines.play.services)

    // Local persistence for game progress (resume at last character)
    implementation(libs.androidx.datastore.preferences)

    //lotti
    implementation(libs.lottie.compose)

    // Navigation between tabs and activity screens
    implementation(libs.androidx.navigation.compose)

    // SVG asset rendering (Coil + its SVG decoder)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    // Play Asset Delivery: downloads the on-demand voice packs
    implementation(libs.play.asset.delivery.ktx)
}