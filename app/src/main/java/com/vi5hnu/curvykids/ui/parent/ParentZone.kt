package com.vi5hnu.curvykids.ui.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.app.Difficulty
import com.vi5hnu.curvykids.ui.app.AppSettings
import com.vi5hnu.curvykids.ui.app.AppUiState
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.theme.Coral
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.audio.packs.VoicePack
import com.vi5hnu.curvykids.audio.packs.VoicePackRepository
import com.vi5hnu.curvykids.ui.voice.VoicePackPanel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.vi5hnu.curvykids.ui.theme.InkFaint
import com.vi5hnu.curvykids.billing.ProState
import com.vi5hnu.curvykids.billing.StoreState
import com.vi5hnu.curvykids.ui.theme.KidBerry
import com.vi5hnu.curvykids.ui.theme.KidGrape
import com.vi5hnu.curvykids.ui.theme.Teal
import com.vi5hnu.curvykids.ui.theme.TintSun
import com.vi5hnu.curvykids.ui.theme.TintTeal

/**
 * ParentZone bottom sheet — shows stars earned, items mastered, the sound toggle, and CurvyKids Pro.
 * The toggle state is persisted via [settings] / [onSoundEffects] so it survives sheet
 * dismissals and app restarts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentZone(
    appState: AppUiState,
    settings: AppSettings,
    onSoundEffects: (Boolean) -> Unit,
    onDifficulty: (Difficulty) -> Unit,
    voicePacks: VoicePackRepository,
    proState: ProState,
    onOpenPro: () -> Unit,
    onRedeemCode: () -> Unit,
    onResetProgress: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Two-step so a mis-tap cannot wipe weeks of a child's progress.
    var confirmingReset by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "Parent Zone",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Ink,
            )
            Spacer(Modifier.height(14.dp))

            // ── Stat tiles ─────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                StatTile(
                    label = "Stars earned",
                    value = "${appState.stars}",
                    tint = TintSun,
                    valueColor = Color(0xFFF2A93B),
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = "Items mastered",
                    value = "${appState.mastered.size}",
                    tint = TintTeal,
                    valueColor = Teal,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(18.dp))

            // ── CurvyKids Pro ─────────────────────────────────────────────
            // Already past the parent gate here, so this opens the purchase screen directly.
            ProRow(proState, onOpenPro)
            if (!proState.owned) {
                // A promo code from the Play Console unlocks Pro without the purchase screen.
                TextButton(onClick = onRedeemCode, modifier = Modifier.align(Alignment.End)) {
                    Text("Have a promo code? Redeem it", fontWeight = FontWeight.Bold, fontSize = 13.sp,
                        color = KidGrape)
                }
            }
            Spacer(Modifier.height(14.dp))

            // ── Toggle ────────────────────────────────────────────────────
            ToggleRow(label = "Sound effects & voice", checked = settings.soundEffects, onChange = onSoundEffects)

            HorizontalDivider(color = InkFaint.copy(alpha = 0.35f))

            // ── Difficulty ────────────────────────────────────────────────
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Difficulty",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Ink,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Easy gives fewer choices per question and more tries before moving on.",
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = Ink.copy(alpha = 0.6f),
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Difficulty.entries.forEach { level ->
                    val selected = settings.difficulty == level
                    Surface(
                        onClick = { onDifficulty(level) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (selected) TintTeal else Color(0xFFF4FAFE),
                        border = if (selected) BorderStroke(2.dp, Teal) else null,
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = level.name.lowercase().replaceFirstChar { it.uppercase() },
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = if (selected) Teal else Ink.copy(alpha = 0.6f),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = InkFaint.copy(alpha = 0.35f))
            Spacer(Modifier.height(14.dp))

            // ── Voice packs ───────────────────────────────────────────────
            // Recorded voices download per category; fetching them here means a child never
            // meets the "ask a grown-up" screen.
            Text(
                text = "Voice packs",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Ink,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Recorded voices for the Hindi activities. Download once, then they work offline.",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Ink.copy(alpha = 0.6f),
            )
            Spacer(Modifier.height(10.dp))
            val packStates by voicePacks.states.collectAsState()
            LaunchedEffect(Unit) { voicePacks.refresh() }
            VoicePackPanel(VoicePack.entries, packStates, voicePacks)

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = InkFaint.copy(alpha = 0.35f))
            Spacer(Modifier.height(14.dp))

            // ── Reset ─────────────────────────────────────────────────────
            if (!confirmingReset) {
                Surface(
                    onClick = { confirmingReset = true },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF1EE),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Reset progress",
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Coral,
                        )
                    }
                }
            } else {
                Text(
                    text = "This erases all stars, stickers and letters learned. It cannot be undone.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Coral,
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        onClick = { confirmingReset = false },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF4FAFE),
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Keep it", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp, color = Ink)
                        }
                    }
                    CandyButton(
                        onClick = { onResetProgress(); confirmingReset = false },
                        modifier = Modifier.weight(1f).height(48.dp),
                        containerColor = Coral,
                    ) {
                        Text("Erase", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            CandyButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                // Teal, not coral: warm colour is reserved for the destructive action below,
                // otherwise the safe button is the one that looks dangerous.
                containerColor = Teal,
            ) {
                Text(
                    text = "Done",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    tint: Color,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = tint,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(14.dp),
        ) {
            Text(
                text = value,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = valueColor,
            )
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Ink.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Ink,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Teal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = InkFaint,
            ),
        )
    }
}

/**
 * "Pro unlocked" once bought; otherwise the current price (and the saving, when a Play Console
 * offer is live), tapping through to the purchase screen.
 */
@Composable
private fun ProRow(state: ProState, onOpen: () -> Unit) {
    val offer = (state.store as? StoreState.Ready)?.offer
    Surface(
        onClick = onOpen,
        enabled = !state.owned,
        shape = RoundedCornerShape(18.dp),
        color = KidGrape.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text("CurvyKids Pro", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp, color = KidGrape)
                Text(
                    when {
                        state.owned -> "Unlocked. Thank you!"
                        state.pending -> "Payment pending"
                        else -> "Every activity, no ads. One-time payment."
                    },
                    fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Ink.copy(alpha = 0.6f),
                )
            }
            if (!state.owned && offer != null) {
                Column(horizontalAlignment = Alignment.End) {
                    offer.percentOff?.takeIf { offer.isDiscount }?.let {
                        Text("$it% OFF", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = KidBerry)
                    }
                    Text(offer.price, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp, color = Ink)
                }
            } else if (state.owned) {
                Text("✓", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = KidGrape)
            }
        }
    }
}
