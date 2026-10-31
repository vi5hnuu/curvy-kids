// On-demand voice pack, delivered by Google Play when a topic that needs it is opened.
// See VoicePack (app/src/main/java/.../audio/packs/VoicePack.kt) for the topic ↔ pack mapping.
plugins {
    alias(libs.plugins.android.asset.pack)
}

assetPack {
    packName.set("voice_hi_barakhadi")
    dynamicDelivery {
        deliveryType.set("on-demand")
    }
}
