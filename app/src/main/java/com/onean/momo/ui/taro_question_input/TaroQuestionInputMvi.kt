package com.onean.momo.ui.taro_question_input

import androidx.compose.foundation.text.input.TextFieldState

data class TaroQuestionInputUiState(
    val topic: String,
    val questionTextInput: TextFieldState = TextFieldState()
)

sealed class TaroQuestionInputDest{
    data class DrawCardScreen(val questionText: String): TaroQuestionInputDest()
}