import java.util.Properties

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

/**
 * Play licence key (Play Console > Monetization setup > Licensing), used to check that a Pro
 * purchase really came from Play. Kept in local.properties as play.licenseKey, out of git.
 */
val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.isFile }?.inputStream()?.use { load(it) }
}
val playLicenseKey: String = localProperties.getProperty("play.licenseKey").orEmpty()

/**
 * AdMob app id (AdMob > Apps > App settings; the one with "~"), kept in local.properties as
 * admob.appId. Debug builds use Google's sample app id and test ad unit, so no real ad is ever
 * shown (or clicked) while developing.
 */
val admobAppId: String = localProperties.getProperty("admob.appId").orEmpty()
val admobSampleAppId = "ca-app-pub-3940256099942544~3347511713"
val rewardedUnitId = "ca-app-pub-4715945578201106/8253929843"
val rewardedTestUnitId = "ca-app-pub-3940256099942544/5224354917"

/**
 * Debug builds sell Pro through a stand-in store (see billing/FakeProRepository) because real Play
 * billing only answers apps installed from a Play test track. Build with -PrealBilling to use Play.
 */
val realBillingInDebug = (findProperty("realBilling") as String?)?.toBoolean() ?: false

/** How long the stand-in store's launch offer lasts; -PfakeOfferEnds=60 to watch one end. */
val fakeOfferSeconds = (findProperty("fakeOfferEnds") as String?)?.toLongOrNull() ?: (3 * 24 * 3600L)

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
        buildConfigField("String", "PLAY_LICENSE_KEY", "\"$playLicenseKey\"")
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "FAKE_BILLING", "${!realBillingInDebug}")
            buildConfigField("long", "FAKE_OFFER_SECONDS", "${fakeOfferSeconds}L")
            buildConfigField("String", "REWARDED_AD_UNIT", "\"$rewardedTestUnitId\"")
            manifestPlaceholders["admobAppId"] = admobSampleAppId
        }
        release {
            // R8 shrinking + resource shrinking. The app ships a 4.7 MB hand-drawn SVG asset
            // set, so keeping the code and unused resources trimmed matters for install size.
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("boolean", "FAKE_BILLING", "false")
            buildConfigField("long", "FAKE_OFFER_SECONDS", "0L")
            buildConfigField("String", "REWARDED_AD_UNIT", "\"$rewardedUnitId\"")
            manifestPlaceholders["admobAppId"] = admobAppId
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
        buildConfig = true
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

/**
 * A release without these would ship broken: no ads (the SDK refuses a missing app id) and no Pro
 * (purchases can't be verified, so none is granted). Fail the release build instead.
 */
tasks.matching { it.name.matches(Regex("(assemble|bundle|install)Release")) }.configureEach {
    doFirst {
        val missing = listOf("admob.appId" to admobAppId, "play.licenseKey" to playLicenseKey)
            .filter { it.second.isBlank() }.map { it.first }
        if (missing.isNotEmpty()) throw GradleException("Set ${missing.joinToString()} in local.properties for a release build")
    }
}

/**
 * Classes the Compose compiler can't see are stable on its own (they hold a Set, or a lambda);
 * declaring them lets screens that receive one skip redrawing when it hasn't changed.
 */
composeCompiler {
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("app/compose_stability.conf")
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

    // Play Billing: the one-time CurvyKids Pro unlock and its offers
    implementation(libs.play.billing)

    // AdMob: rewarded videos that open a Pro item for the day (free users only, child-directed)
    implementation(libs.play.services.ads)
}