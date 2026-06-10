package com.vi5hnu.curvykids.recognition

import android.util.Log
import com.google.mlkit.common.MlKitException
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.RecognitionContext
import com.vi5hnu.curvykids.models.HttpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import com.google.mlkit.vision.digitalink.WritingArea as MlKitWritingArea

/**
 * [Recognizer] backed by Google ML Kit Digital Ink Recognition (fully on-device after the
 * one-time model download). Replaces the old WebView JS-bridge path: strokes are passed as
 * in-memory [Stroke]s (no JSON marshalling) and we feed ML Kit a [RecognitionContext] hint
 * to improve single-character accuracy.
 */
class MlKitInkRecognizer(
    private val modelIdentifier: DigitalInkRecognitionModelIdentifier =
        DigitalInkRecognitionModelIdentifier.EN_US,
) : Recognizer {

    private companion object {
        const val TAG = "MLKIT"
    }

    private var recognizer: DigitalInkRecognizer? = null

    private val _ready = MutableStateFlow<HttpState?>(null)
    override val ready: StateFlow<HttpState?> = _ready.asStateFlow()

    override suspend fun prepare() {
        _ready.value = HttpState.loading()
        try {
            val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
            val modelManager = RemoteModelManager.getInstance()

            if (!modelManager.isModelDownloaded(model).await()) {
                Log.d(TAG, "Model ${modelIdentifier.languageTag} not found, downloading...")
                modelManager.download(model, DownloadConditions.Builder().build()).await()
                Log.i(TAG, "Model ${modelIdentifier.languageTag} downloaded.")
            } else {
                Log.i(TAG, "Model ${modelIdentifier.languageTag} already downloaded.")
            }

            recognizer = DigitalInkRecognition.getClient(
                DigitalInkRecognizerOptions.builder(model).build()
            )
            _ready.value = HttpState.success()
        } catch (e: MlKitException) {
            Log.e(TAG, "ML Kit initialization error: ${e.message}", e)
            _ready.value = HttpState.error("Something went wrong")
        } catch (e: Exception) {
            Log.e(TAG, "General initialization error: ${e.message}", e)
            _ready.value = HttpState.error("Something went wrong")
        }
    }

    override suspend fun recognize(
        strokes: List<Stroke>,
        writingArea: WritingArea?,
        preContext: String?,
    ): List<String> {
        val client = recognizer
        if (_ready.value?.success != true || client == null) {
            Log.e(TAG, "Recognizer not ready; cannot recognize.")
            return emptyList()
        }
        if (strokes.isEmpty()) return emptyList()

        return try {
            val ink = strokes.toInk()
            val context = buildContext(writingArea, preContext)
            // The (ink, context) overload requires a non-null context; fall back otherwise.
            val task = if (context != null) client.recognize(ink, context) else client.recognize(ink)
            val result = task.await()
            // Return whatever candidates ML Kit gives — never index a fixed position
            // (the old bridge read candidates[1]/[2] and could crash).
            result.candidates.map { it.text }
        } catch (e: MlKitException) {
            Log.e(TAG, "Recognition failed: ${e.message}", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "General recognition error: ${e.message}", e)
            emptyList()
        }
    }

    override fun release() {
        recognizer?.close()
        recognizer = null
        Log.d(TAG, "DigitalInkRecognizer closed.")
    }

    /** Builds an optional recognition context hint (writing area + preceding text). */
    private fun buildContext(
        writingArea: WritingArea?,
        preContext: String?,
    ): RecognitionContext? {
        if (writingArea == null && preContext.isNullOrEmpty()) return null
        return RecognitionContext.builder()
            .apply {
                writingArea?.let { setWritingArea(MlKitWritingArea(it.width, it.height)) }
                if (!preContext.isNullOrEmpty()) setPreContext(preContext)
            }
            .build()
    }

    /** Converts in-memory strokes into an ML Kit [Ink]. */
    private fun List<Stroke>.toInk(): Ink {
        val builder = Ink.builder()
        forEach { stroke ->
            val strokeBuilder = Ink.Stroke.builder()
            stroke.points.forEach { p ->
                strokeBuilder.addPoint(Ink.Point.create(p.x, p.y, p.t))
            }
            builder.addStroke(strokeBuilder.build())
        }
        return builder.build()
    }
}
