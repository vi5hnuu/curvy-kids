// On-demand Pro voice pack: Google Play downloads it only when the app asks, which it does once
// a Pro item is open to the child (Pro owned, or opened for today with a video) and is tapped,
// or when a grown-up taps "Download all" in the Parent Zone. Families without Pro never fetch it.
// Which recordings live here is decided by VoicePack.homeOf (app/src/main/java/.../audio/packs/
// VoicePack.kt); VoiceScriptTest checks every recording is in its module.
plugins {
    alias(libs.plugins.android.asset.pack)
}

assetPack {
    packName.set("voice_pro_letters")
    dynamicDelivery {
        deliveryType.set("on-demand")
    }
}
