package com.vi5hnu.curvykids.data.export

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Saves the child's Free Draw artwork to the device gallery, and hands it to the share sheet.
 *
 * Writes through [MediaStore] rather than to app-private storage so the picture shows up in the
 * gallery like any other photo — that is what makes it feel kept, and it is also why no storage
 * permission is needed (the app only ever touches the row it created).
 */
class ArtworkStore(private val context: Context) {

    /**
     * Writes [bitmap] into `Pictures/CurvyKids`.
     *
     * @return the saved image's content URI, or null if the write failed (device out of space,
     *         provider unavailable). The caller should tell the child nothing was saved rather
     *         than pretending it worked.
     */
    suspend fun save(bitmap: Bitmap): Uri? = withContext(Dispatchers.IO) {
        val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "curvykids-$stamp.png")
            put(MediaStore.Images.Media.MIME_TYPE, MIME_TYPE)
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$ALBUM")
            // Hide the row until the bytes are actually written, so no gallery app can show a
            // half-written image.
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = runCatching {
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }.getOrNull() ?: return@withContext null

        val written = runCatching {
            resolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            } ?: false
        }.getOrElse {
            Log.e(TAG, "Failed writing artwork", it)
            false
        }

        if (!written) {
            runCatching { resolver.delete(uri, null, null) }
            return@withContext null
        }

        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        runCatching { resolver.update(uri, values, null, null) }
        uri
    }

    /** Opens the system share sheet for a previously saved [uri]. */
    fun share(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(intent, "Share drawing")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private companion object {
        const val TAG = "ArtworkStore"
        const val MIME_TYPE = "image/png"
        const val ALBUM = "CurvyKids"
    }
}
