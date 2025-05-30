package com.vi5hnu.curvykids.components

import androidx.compose.runtime.*
import com.airbnb.lottie.compose.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun LottieViewer(
    modifier: Modifier = Modifier,
    assetName: String,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(assetName))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = iterations,
        isPlaying = true
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress }
        )
    }
}
