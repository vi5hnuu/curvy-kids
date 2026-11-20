package com.vi5hnu.curvykids.ui.pro

import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.data.content.KidAssets
import com.vi5hnu.curvykids.ui.components.SvgArt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.vi5hnu.curvykids.ui.theme.KidSun

/**
 * Told to the grown-up of a free user who is offline: the free activities still work, the rest
 * open with a short video (which needs the internet) or with Pro, which plays everything
 * offline. Free play is never blocked: families use the app in cars and on flights, and a
 * blocked children's app earns poor reviews and risks Teacher Approved, while the videos (the
 * free tier's only income) can't play offline anyway.
 *
 * Quiet like the offer banner ([GrownUpNotice]); [onOpen] is expected to go through the parent
 * gate to the purchase screen. [onHide] closes it for this visit only.
 */
@Composable
fun OfflineNotice(onOpen: () -> Unit, onHide: () -> Unit, modifier: Modifier = Modifier) {
    GrownUpNotice(
        iconColor = KidSun,
        // Our own cloud art: "the internet" to a grown-up glancing at it, with no words needed.
        icon = { SvgArt(KidAssets.weather("cloud"), modifier = Modifier.size(22.dp)) },
        title = AnnotatedString("Grown-ups: you're offline"),
        body = "Free play works. Connect for videos, or get Pro to play all offline.",
        hideLabel = "Hide this notice",
        onOpen = onOpen,
        onHide = onHide,
        modifier = modifier,
        maxBodyLines = 2,
    )
}
