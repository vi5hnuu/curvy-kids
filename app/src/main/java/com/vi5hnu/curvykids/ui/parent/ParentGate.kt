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
import com.vi5hnu.curvykids.ui.theme.Coral
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkFaint
import com.vi5hnu.curvykids.ui.theme.InkSoft

/**
 * ParentGate bottom sheet — a sum written in words (see [GateQuestion]) that only adults can
 * solve. Opened from ParentTab; calls [onPass] when the correct answer is entered. A wrong answer
 * brings a new question, so the gate can't be passed by guessing the same one over and over.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentGate(
    onPass: () -> Unit,
    onDismiss: () -> Unit,
) {
    // Saveable so a rotation does not swap the question out from under a parent mid-answer.
    var question by rememberSaveable(stateSaver = GateQuestion.Saver) { mutableStateOf(GateQuestion.random()) }

    var input by rememberSaveable { mutableStateOf("") }
    var showWrong by rememberSaveable { mutableStateOf(false) }
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
                text = "Add the numbers and type the answer:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = InkSoft,
            )
            Text(
                text = question.text,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Ink,
            )
            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it.filter { c -> c.isDigit() }.take(3)
                    showWrong = false
                },
                placeholder = { Text("Answer", color = InkFaint) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
            )

            Spacer(Modifier.height(14.dp))

            if (showWrong) {
                Text(
                    text = "Not quite — here's a new one.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Coral,
                )
                Spacer(Modifier.height(8.dp))
            }

            // The button deliberately looks the same whether or not the typed value is correct,
            // and only checks on tap. Colouring it green the moment the digits matched turned the
            // gate into a game a child could brute-force by watching for the colour change.
            CandyButton(
                onClick = {
                    if (input.toIntOrNull() == question.answer) {
                        onPass()
                    } else {
                        showWrong = true
                        input = ""
                        question = GateQuestion.random()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                containerColor = Green,
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
