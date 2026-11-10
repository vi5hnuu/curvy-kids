package com.vi5hnu.curvykids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vi5hnu.curvykids.ui.app.AppViewModel
import com.vi5hnu.curvykids.ui.game.GameViewModel
import com.vi5hnu.curvykids.ui.navigation.AppNavGraph
import com.vi5hnu.curvykids.ui.theme.CurvyKidsTheme

/**
 * Single-activity host. Owns the two activity-scoped ViewModels:
 *   - [AppViewModel]  — global stars/streak/mastered/badges state (persisted via DataStore)
 *   - [GameViewModel] — ML Kit ink recognition + tracing game loop
 * Both are passed into [AppNavGraph] so any descendant screen can access them without
 * threading them through the composition tree manually.
 */
class MainActivity : ComponentActivity() {

    /**
     * A payment that was pending (cash, slow card) may have completed while the app was in the
     * background; Play doesn't tell a closed purchase sheet, so ask again on every return.
     */
    override fun onResume() {
        super.onResume()
        (application as CurvyKidsApp).pro.refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurvyKidsTheme {
                val appViewModel: AppViewModel = viewModel(factory = AppViewModel.factory(this))
                val gameViewModel: GameViewModel = viewModel(factory = GameViewModel.factory(this))

                AppNavGraph(
                    appViewModel = appViewModel,
                    gameViewModel = gameViewModel,
                    voicePacks = (application as CurvyKidsApp).voicePacks,
                    pro = (application as CurvyKidsApp).pro,
                    access = (application as CurvyKidsApp).access,
                    ads = (application as CurvyKidsApp).ads,
                    adUnlocks = (application as CurvyKidsApp).adUnlocks,
                )
            }
        }
    }
}
