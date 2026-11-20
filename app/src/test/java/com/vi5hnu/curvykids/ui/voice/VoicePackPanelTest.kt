package com.vi5hnu.curvykids.ui.voice

import com.vi5hnu.curvykids.audio.packs.FailReason
import com.vi5hnu.curvykids.audio.packs.PackState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VoicePackPanelTest {
    @Test fun nothingToDoWhenAllReady() = assertNull(nextAction(listOf(PackState.Ready, PackState.Ready)))

    @Test fun downloadWhenMissing() =
        assertEquals(PanelAction.DOWNLOAD, nextAction(listOf(PackState.Ready, PackState.NotDownloaded(10))))

    @Test fun waitWhileDownloading() =
        assertEquals(PanelAction.WAIT, nextAction(listOf(PackState.Downloading(1, 10), PackState.NotDownloaded(10))))

    @Test fun confirmationComesFirst() =
        assertEquals(PanelAction.CONFIRM, nextAction(listOf(PackState.Downloading(1, 10), PackState.NeedsConfirmation)))

    @Test fun retryAfterFailure() =
        assertEquals(PanelAction.RETRY, nextAction(listOf(PackState.Failed(FailReason.OFFLINE), PackState.NotDownloaded(1))))

    @Test fun checkWhileUnknown() = assertEquals(PanelAction.CHECK, nextAction(listOf(PackState.Checking)))
}
