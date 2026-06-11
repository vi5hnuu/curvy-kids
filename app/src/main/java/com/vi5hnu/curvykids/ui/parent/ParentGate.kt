package com.vi5hnu.curvykids.ui.parent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkFaint
import com.vi5hnu.curvykids.ui.theme.InkSoft

/**
 * ParentGate bottom sheet — a multiplication puzzle that only adults can solve quickly.
 * Opened from ParentTab; calls [onPass] when the correct answer is entered.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentGate(
    onPass: () -> Unit,
    onDismiss: () -> Unit,
) {
    // Randomise a and b once per gate open
    val a = remember { (2..7).random() }
    val b = remember { (2..6).random() }
    val answer = a * b

    var input by rememberSaveable { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        containerColor = Color.White,
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp).padding(bottom = 32.dp)) {
            Text(
                text = "Grown-ups only",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Ink,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Solve to continue:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = InkSoft,
            )
            Text(
                text = "$a × $b = ?",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Ink,
            )
            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter { c -> c.isDigit() }.take(3) },
                placeholder = { Text("Answer", color = InkFaint) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
            )

            Spacer(Modifier.height(14.dp))

            val correct = input.toIntOrNull() == answer
            CandyButton(
                onClick = { if (correct) onPass() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                containerColor = if (correct) Green else InkFaint,
            ) {
                Text(
                    text = "Unlock",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                )
            }
        }
    }
}
