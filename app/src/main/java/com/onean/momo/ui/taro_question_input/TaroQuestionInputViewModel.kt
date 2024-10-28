package com.onean.momo.ui.taro_question_input

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel(assistedFactory = TaroQuestionInputViewModelFactory::class)
class TaroQuestionInputViewModel
@AssistedInject constructor(
    @Assisted("topic") private val topic: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaroQuestionInputUiState(topic = topic))
    val uiState = _uiState.asStateFlow()
    private val _navEvent = Channel<TaroQuestionInputDest>()
    val navEvent = _navEvent.receiveAsFlow()

    fun onQuestionSubmit() {
        val questionText = _uiState.value.questionTextInput.text.toString()
        if (questionText.isBlank()) {
            return
        }
        _navEvent.trySend(
            TaroQuestionInputDest.DrawCardScreen(questionText)
        )
    }
}

@AssistedFactory
interface TaroQuestionInputViewModelFactory {
    fun create(
        @Assisted("topic") topic: String
    ): TaroQuestionInputViewModel
}
