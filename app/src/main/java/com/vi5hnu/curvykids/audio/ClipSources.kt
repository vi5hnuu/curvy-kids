package com.vi5hnu.curvykids.audio

import android.content.res.AssetManager
import android.media.MediaPlayer
import com.vi5hnu.curvykids.audio.packs.VoicePack
import com.vi5hnu.curvykids.audio.packs.VoicePackRepository
import java.io.File

/** A recording found by a [ClipSource], ready to be handed to a [MediaPlayer]. */
fun interface ClipFile {
    fun attachTo(player: MediaPlayer)
}

/**
 * Somewhere recordings can live. [ClipPlayer] asks each source in turn, so a new place to keep
 * clips (a download, a cache) is a new source rather than a change to the player.
 */
fun interface ClipSource {
    /** The recording for [clip], or null when this source doesn't have it. */
    fun find(clip: VoiceClip): ClipFile?

    companion object {
        /** File types tried for each clip, in order. */
        val EXTENSIONS = listOf("m4a", "mp3", "ogg", "wav")
    }
}

/**
 * Clips inside the app's own `assets/voice/`. Stored uncompressed by the build, which is what
 * [AssetManager.openFd] needs.
 */
class BundledClipSource(private val assets: AssetManager) : ClipSource {

    /** Folder → file names, read once per folder. Asset listing is slow, lookups aren't. */
    private val listing = mutableMapOf<String, Set<String>>()

    override fun find(clip: VoiceClip): ClipFile? {
        val folder = clip.basePath.substringBeforeLast('/')
        val files = listing.getOrPut(folder) {
            runCatching { assets.list(folder)?.toSet() }.getOrNull() ?: emptySet()
        }
        val path = ClipSource.EXTENSIONS.firstOrNull { "${clip.key}.$it" in files }
            ?.let { "${clip.basePath}.$it" } ?: return null
        return ClipFile { player ->
            assets.openFd(path).use { fd -> player.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length) }
        }
    }
}

/** Clips inside a [VoicePack] that Play has downloaded to the device. */
class PackClipSource(private val packs: VoicePackRepository) : ClipSource {

    override fun find(clip: VoiceClip): ClipFile? {
        val pack = VoicePack.forClip(clip) ?: return null
        val dir = packs.assetsDir(pack) ?: return null
        val file = ClipSource.EXTENSIONS
            .map { File(dir, "${clip.basePath}.$it") }
            .firstOrNull { it.isFile } ?: return null
        return ClipFile { player -> player.setDataSource(file.path) }
    }
}
