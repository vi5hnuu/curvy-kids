package com.vi5hnu.curvykids.recognition

import android.util.Log
import com.google.mlkit.common.MlKitException
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.recognition.Ink
import com.google.mlkit.vision.digitalink.recognition.RecognitionContext
import com.vi5hnu.curvykids.models.HttpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import com.google.mlkit.vision.digitalink.recognition.WritingArea as MlKitWritingArea

/**
 * [Recognizer] backed by Google ML Kit Digital Ink Recognition (fully on-device after the
 * one-time per-language model download).
 *
 * Supports multiple scripts: a [DigitalInkRecognizer] is built and cached lazily per BCP-47
 * language tag (e.g. "en-US" for A-Z/0-9, "hi" for Devanagari). [ensure] switches the active
 * model — downloading it the first time — so the same recognizer serves both the English and
 * Hindi tracing tracks. Strokes are passed in-memory (no JSON marshalling) and a
 * [RecognitionContext] hint improves single-character accuracy.
 */
class MlKitInkRecognizer(
    private val defaultLanguageTag: String = "en-US",
) : Recognizer {

    private companion object {
        const val TAG = "MLKIT"
    }

    /** Built recognizers keyed by language tag; reused across level switches. */
    private val clients = mutableMapOf<String, DigitalInkRecognizer>()

    /** Serialises model download/build so concurrent [ensure] calls don't race on [clients]. */
    private val mutex = Mutex()

    @Volatile
    private var activeTag: String = defaultLanguageTag

    private val _ready = MutableStateFlow<HttpState?>(null)
    override val ready: StateFlow<HttpState?> = _ready.asStateFlow()

    override suspend fun prepare() {
        ensure(defaultLanguageTag)
    }

    override suspend fun ensure(languageTag: String) {
        // Already active and built → nothing to do (keeps level-switches instant once cached).
        if (languageTag == activeTag && clients[languageTag] != null && _ready.value?.success == true) return

        _ready.value = HttpState.loading()
        try {
            val client = mutex.withLock {
                clients[languageTag] ?: buildClient(languageTag)?.also { clients[languageTag] = it }
            }
            if (client != null) {
                activeTag = languageTag
                _ready.value = HttpState.success()
            } else {
                _ready.value = HttpState.error("Something went wrong")
            }
        } catch (e: MlKitException) {
            Log.e(TAG, "ML Kit init error for $languageTag: ${e.message}", e)
            _ready.value = HttpState.error("Something went wrong")
        } catch (e: Exception) {
            Log.e(TAG, "General init error for $languageTag: ${e.message}", e)
            _ready.value = HttpState.error("Something went wrong")
        }
    }

    /** Downloads (if needed) and builds the recognizer for [languageTag]. */
    private suspend fun buildClient(languageTag: String): DigitalInkRecognizer? {
        val identifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag(languageTag)
            ?: run {
                Log.e(TAG, "No Digital Ink model for language tag '$languageTag'")
                return null
            }
        val model = DigitalInkRecognitionModel.builder(identifier).build()
        val modelManager = RemoteModelManager.getInstance()

        if (!modelManager.isModelDownloaded(model).await()) {
            Log.d(TAG, "Model $languageTag not found, downloading...")
            modelManager.download(model, DownloadConditions.Builder().build()).await()
            Log.i(TAG, "Model $languageTag downloaded.")
        } else {
            Log.i(TAG, "Model $languageTag already downloaded.")
        }
        return DigitalInkRecognition.getClient(DigitalInkRecognizerOptions.builder(model).build())
    }

    override suspend fun recognize(
        strokes: List<Stroke>,
        writingArea: WritingArea?,
        preContext: String?,
    ): List<String> {
        val client = clients[activeTag]
        if (_ready.value?.success != true || client == null) {
            Log.e(TAG, "Recognizer not ready (tag=$activeTag); cannot recognize.")
            return emptyList()
        }
        if (strokes.isEmpty()) return emptyList()

        val ink = strokes.toInk()
        // Building the context must never crash recognition; fall back to no hint on failure.
        val context = runCatching { buildContext(writingArea, preContext) }.getOrNull()

        // Try with the writing-area hint first. The hint usually improves single-character
        // accuracy, but on some devices/models it can misbehave (throw or return nothing),
        // so fall back to plain recognition rather than failing the child's answer.
        runCatching { recognizeWith(client, ink, context) }
            .onSuccess { candidates ->
                Log.d(TAG, "Recognized (tag=$activeTag, context=${context != null}): $candidates")
                if (candidates.isNotEmpty() || context == null) return candidates
            }
            .onFailure { Log.e(TAG, "Recognition with context failed: ${it.message}", it) }

        if (context == null) return emptyList()
        return runCatching { recognizeWith(client, ink, null) }
            .onSuccess { Log.d(TAG, "Recognized (fallback, no context): $it") }
            .getOrDefault(emptyList())
    }

    /** Runs a single recognition pass; [context] may be null to use the plain overload. */
    private suspend fun recognizeWith(
        client: DigitalInkRecognizer,
        ink: Ink,
        context: RecognitionContext?,
    ): List<String> {
        val task = if (context != null) client.recognize(ink, context) else client.recognize(ink)
        // Never index a fixed candidate position (the old bridge read [1]/[2] and could crash).
        return task.await().candidates.map { it.text }
    }

    override fun release() {
        clients.values.forEach { it.close() }
        clients.clear()
        Log.d(TAG, "All DigitalInkRecognizers closed.")
    }

    /** Builds an optional recognition context hint (writing area + preceding text). */
    private fun buildContext(
        writingArea: WritingArea?,
        preContext: String?,
    ): RecognitionContext? {
        if (writingArea == null && preContext.isNullOrEmpty()) return null
        // ML Kit's builder REQUIRES preContext to be set, even if empty — omitting it makes
        // build() throw "Missing required properties: preContext".
        return RecognitionContext.builder()
            .setPreContext(preContext ?: "")
            .apply { writingArea?.let { setWritingArea(MlKitWritingArea(it.width, it.height)) } }
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
