package com.vi5hnu.curvykids.jsInterfaces

import android.R
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.mlkit.vision.digitalink.Ink
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vi5hnu.curvykids.commons.InkRecognitionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class Point(val x: Float, val y: Float,val t: Long)
data class Stroke(val points: List<Point>)

class CurvyKidsJsBridge(private val context: Context,private val webView: WebView,private val recodnizer: InkRecognitionHelper ) {
    @JavascriptInterface
    fun vibrate(ms: Long = 200) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    @JavascriptInterface
    fun vibrate() {
        this.vibrate(200);
    }

    @JavascriptInterface
    fun recognizeDrawing(jsonStrokes: String) {

        CoroutineScope(Dispatchers.Main).launch {
            val ink = parseJsonToInk(jsonStrokes)
            var result=recodnizer.recognize(ink,);
            Log.d("MLKITRESULT",result?.candidates.toString());


            val isMatch=listOf<String?>(result?.candidates?.get(0)?.text ,result?.candidates?.get(1)?.text,result?.candidates?.get(2)?.text).joinToString(prefix = "[", postfix = "]") { "'$it'" }
            webView.post {
                webView.evaluateJavascript("onRecognitionResult(${isMatch})", null)
            }
        }
    }

    fun parseJsonToInk(json: String): Ink {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val type = Types.newParameterizedType(List::class.java, Stroke::class.java)
        val adapter: JsonAdapter<List<Stroke>> = moshi.adapter(type)

        val strokeList = adapter.fromJson(json) ?: emptyList()
1
        val builder = Ink.builder()
        strokeList.forEach { stroke ->
            val strokeBuilder = Ink.Stroke.builder()
            stroke.points.forEach { point -> // Renamed 'it' to 'point' for clarity
                strokeBuilder.addPoint(Ink.Point.create(point.x, point.y, point.t))
            }
            builder.addStroke(strokeBuilder.build())
        }
        return builder.build()
    }

//    @JavascriptInterface
//    fun playSound(soundName: String) {
//        // For simplicity, use raw resource files like R.raw.success, R.raw.fail, etc.
//        val soundId = when (soundName) {
//            "success" -> R.raw.success
//            "fail" -> R.raw.fail
//            else -> return
//        }
//        val mediaPlayer = MediaPlayer.create(context, soundId)
//        mediaPlayer.setOnCompletionListener { it.release() }
//        mediaPlayer.start()
//    }

}
