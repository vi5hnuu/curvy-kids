package com.vi5hnu.curvykids.commons

import android.content.Context
import android.util.Log
import com.google.mlkit.common.MlKitException
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.*
import com.vi5hnu.curvykids.models.HttpState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await // Important for suspending on Tasks

class InkRecognitionHelper(private val context: Context, private val scope: CoroutineScope) { // Added CoroutineScope
    private var engRecognizer: DigitalInkRecognizer? = null
    private val TAG = "MLKIT"

    private val _isRecognizerReady = MutableStateFlow<HttpState?>(null)
    val isRecognizerReady: StateFlow<HttpState?> get() = _isRecognizerReady
    
    suspend fun initializeModel(modelIdentifier: DigitalInkRecognitionModelIdentifier = DigitalInkRecognitionModelIdentifier.EN): Boolean {
        try {
            _isRecognizerReady.emit(HttpState.loading()) // Signal that the recognizer is ready
            val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
            val remoteModelManager = RemoteModelManager.getInstance()

            // Check if the model is already downloaded
            val isDownloaded = remoteModelManager.isModelDownloaded(model).await()

            if (!isDownloaded) {
                Log.d(TAG, "Model ${modelIdentifier.languageTag} not found, starting download...")
                remoteModelManager.download(model, DownloadConditions.Builder().build()).await()
                Log.i(TAG, "Model  ${modelIdentifier.languageTag} downloaded successfully.")
            } else {
                Log.i(TAG, "Model  ${modelIdentifier.languageTag} already downloaded.")
            }

            // Now that the model is confirmed to be downloaded, create the recognizer client
            engRecognizer = DigitalInkRecognition.getClient(DigitalInkRecognizerOptions.builder(model).build())
            Log.d(TAG, "DigitalInkRecognizer client created for  ${modelIdentifier.languageTag}.")
            _isRecognizerReady.emit(HttpState.success()) // Signal that the recognizer is ready
            return true
        } catch (e: MlKitException) {
            Log.e(TAG, "ML Kit initialization error: ${e.message}", e)
            _isRecognizerReady.emit(HttpState.error("Something went wrong")) // Signal failure
            return false
        } catch (e: Exception) {
            Log.e(TAG, "General initialization error: ${e.message}", e)
            _isRecognizerReady.emit(HttpState.error("Something went wrong")) // Signal failure
            return false
        }
    }

    suspend fun recognize(ink: Ink): RecognitionResult? {
        // Wait until the recognizer is ready
        val isRecognizerReady=isRecognizerReady.value;
        if (isRecognizerReady?.success!=null &&  isRecognizerReady.success== true) {
            Log.e(TAG, "Recognizer is not ready. Cannot perform recognition.")
            return null
        }

        val currentRecognizer = engRecognizer ?: run {
            Log.e(TAG, "Recognizer is null even after being marked ready. This should not happen.")
            return null
        }

        return try {
            val result = currentRecognizer.recognize(ink).await()
            Log.d(TAG, "Recognition successful.")
            result
        } catch (e: MlKitException) {
            Log.e(TAG, "Recognition failed: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "General recognition error: ${e.message}", e)
            null
        }
    }

    /**
     * Releases the resources held by the DigitalInkRecognizer.
     * Call this when the helper is no longer needed (e.g., in Activity/Fragment onDestroy).
     */
    fun release() {
        engRecognizer?.close()
        engRecognizer = null
        Log.d(TAG, "DigitalInkRecognizer client closed.")
    }
}