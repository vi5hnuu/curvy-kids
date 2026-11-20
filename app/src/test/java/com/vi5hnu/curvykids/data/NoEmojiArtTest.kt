package com.vi5hnu.curvykids.data

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Every picture under assets/art/ is drawn in the app's own kid style by tools/art/draw.
 *
 * Emoji-derived pictures (Twemoji, recognisable by their 36×36 viewBox) were all replaced; this
 * keeps one from slipping back in through a copied file or a re-run of an old conversion script.
 */
class NoEmojiArtTest {

    private val artRoot = File("src/main/assets/art")

    @Test
    fun noPictureUsesTheEmojiCanvas() {
        val emoji = artRoot.walkTopDown()
            .filter { it.isFile && it.extension == "svg" }
            .filter { "viewBox=\"0 0 36 36\"" in it.readText() }
            .map { it.relativeTo(artRoot).path }
            .toList()
        assertTrue("Emoji pictures found, redraw them with tools/art/draw: $emoji", emoji.isEmpty())
    }
}
