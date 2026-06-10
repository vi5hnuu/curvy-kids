package com.vi5hnu.curvykids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vi5hnu.curvykids.ui.game.AlphabetScreen
import com.vi5hnu.curvykids.ui.game.GameViewModel
import com.vi5hnu.curvykids.ui.theme.CurvyKidsTheme

/**
 * Single-activity host for the fully-native CurvyKids game. The previous WebView + JS bridge
 * have been removed: handwriting recognition (ML Kit) is now called directly from native code
 * via [GameViewModel], so the app works offline after the one-time model download.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurvyKidsTheme {
                val viewModel: GameViewModel = viewModel(factory = GameViewModel.factory(this))
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AlphabetScreen(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                }
            }
        }
    }
}
