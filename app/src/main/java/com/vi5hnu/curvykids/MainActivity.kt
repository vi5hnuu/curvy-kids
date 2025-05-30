package com.vi5hnu.curvykids

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.vi5hnu.curvykids.commons.InkRecognitionHelper
import com.vi5hnu.curvykids.components.LottieViewer
import com.vi5hnu.curvykids.jsInterfaces.CurvyKidsJsBridge
import com.vi5hnu.curvykids.models.HttpState
import com.vi5hnu.curvykids.ui.theme.CurvyKidsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var inkRecognitionHelper: InkRecognitionHelper;
    private val activityScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inkRecognitionHelper = InkRecognitionHelper(this, activityScope)
        enableEdgeToEdge()

        // **CRUCIAL**: Start the recognizer initialization on a background thread
        activityScope.launch(Dispatchers.IO) { // Launch on IO dispatcher for model download
            !inkRecognitionHelper.initializeModel(DigitalInkRecognitionModelIdentifier.HI)
        }
        setContent {
            CurvyKidsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding->
                    WebViewScreen(url = "http://192.168.176.77:4600", modifier = Modifier.fillMaxSize().padding(innerPadding),inkRecognitionHelper)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        inkRecognitionHelper.release() // Release ML Kit resources
        activityScope.cancel() // Cancel all coroutines launched in this scope
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CurvyKidsTheme {
        Greeting("Android")
    }
}

@Composable
fun WebViewScreen(url: String, modifier: Modifier = Modifier, inkRecognitionHelper: InkRecognitionHelper) {
    var isWebViewLoading by remember { mutableStateOf(true) }
    val recognizerState by inkRecognitionHelper.isRecognizerReady.collectAsState()

    Box(modifier = modifier) {
        AndroidView(factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                setLayerType(View.LAYER_TYPE_HARDWARE, null) // Enable HW acceleration on WebView
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view,url,favicon);
                        isWebViewLoading = true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view,url);
                        isWebViewLoading = false
                    }

                    override fun onReceivedError(
                        view: WebView,
                        request: WebResourceRequest,
                        error: WebResourceError
                    ) {
                        if (request.isForMainFrame) {
                            loadData(
                                "<html><body><h2>Something went Wrong ${error.description}</h2></body></html>",
                                "text/html",
                                "UTF-8"
                            )
                        }
                    }
                }

                settings.apply {
                    javaScriptEnabled = true
                    safeBrowsingEnabled = true
                    allowFileAccess = false
                    allowContentAccess = false
                    domStorageEnabled = true
                    javaScriptCanOpenWindowsAutomatically = false
                    builtInZoomControls = false
                    displayZoomControls = false
                    mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    WebView.setWebContentsDebuggingEnabled(true)
                }

                removeJavascriptInterface("searchBoxJavaBridge_")
                removeJavascriptInterface("accessibility")
                removeJavascriptInterface("accessibilityTraversal")
                addJavascriptInterface(CurvyKidsJsBridge(context,this,inkRecognitionHelper), "CurvyKidsJsBridge")
                loadUrl(url)
            }
        })

            // Show your loader composable here
            if(recognizerState?.success!=true || isWebViewLoading){
                Box(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent() // Don't do anything, just consume
                                }
                            }
                        }
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isWebViewLoading || recognizerState?.loading==true) {
                        LottieViewer(assetName = "baby-loading.lottie")
                    }else if(recognizerState?.error!=null){
                        LottieViewer(assetName = "baby-bottom.lottie")
                    }

                }
            }
    }
}
