package com.vi5hnu.curvykids.recognition

import com.vi5hnu.curvykids.models.HttpState
import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction over a handwriting recognizer.
 *
 * The UI/ViewModel depends on this interface rather than on ML Kit directly (Dependency
 * Inversion), so the recognition backend can be swapped — e.g. an on-device ML Kit model
 * today, a different engine or a fake for tests tomorrow — without touching the game logic.
 */
interface Recognizer {

    /**
     * Readiness of the recognizer. `null` before [prepare] is called, then loading →
     * success / error. The UI observes this to gate input and show a loader.
     */
    val ready: StateFlow<HttpState?>

    /** Downloads (if needed) and initialises the recognition model. Safe to call once. */
    suspend fun prepare()

    /**
     * Recognises the drawn [strokes] and returns the candidate texts, best-first.
     *
     * @param writingArea optional bounds hint that biases recognition toward a single
     *        character drawn in that region.
     * @param preContext optional preceding text hint (e.g. the level) for the model.
     * @return ordered candidate strings (possibly empty). Never throws for recognition
     *         failures — returns an empty list instead.
     */
    suspend fun recognize(
        strokes: List<Stroke>,
        writingArea: WritingArea? = null,
        preContext: String? = null,
    ): List<String>

    /** Releases native resources. Call from the owner's onCleared/onDestroy. */
    fun release()
}
