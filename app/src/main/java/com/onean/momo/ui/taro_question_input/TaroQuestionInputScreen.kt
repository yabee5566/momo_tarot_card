package com.onean.momo.ui.taro_question_input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaroQuestionInputScreen(
    uiState: TaroQuestionInputUiState,
    onQuestionSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    TaroQuestionInputScreen(
        modifier = modifier,
        topic = uiState.topic,
        questionTextFieldState = uiState.questionTextInput,
        onQuestionSubmit = onQuestionSubmit
    )
}

@Composable
fun TaroQuestionInputScreen(
    topic: String,
    questionTextFieldState: TextFieldState,
    onQuestionSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 40.dp),
            text = "The topic you chose is: $topic",
            fontSize = 22.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(40.dp))
        BasicTextField(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(fontSize = 18.sp),
            state = questionTextFieldState,
            decorator = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (questionTextFieldState.text.isEmpty()) { // Check if text is empty
                        Text(
                            text = "Enter your question here",
                            color = Color.Gray,
                            fontSize = 18.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            enabled = questionTextFieldState.text.isNotEmpty(),
            onClick = onQuestionSubmit
        ) {
            Text(text = "Submit")
        }
    }
}

@Preview
@Composable
private fun TaroQuestionInputScreenPreview() {
    TaroQuestionInputScreen(
        modifier = Modifier.background(Color.White),
        topic = "Love",
        questionTextFieldState = TextFieldState(),
        onQuestionSubmit = {}
    )
}
