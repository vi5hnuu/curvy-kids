// Fast-follow voice pack: Google Play downloads it by itself right after the app is installed or
// updated, without the app being opened. The app also asks for it on start (CurvyKidsApp), and a
// topic opened before it arrives waits on VoicePackGate's loading screen.
// See VoicePack (app/src/main/java/.../audio/packs/VoicePack.kt) for the topic ↔ pack mapping.
plugins {
    alias(libs.plugins.android.asset.pack)
}

assetPack {
    packName.set("voice_hi_barakhadi")
    dynamicDelivery {
        deliveryType.set("fast-follow")
    }
}
