package com.onean.momo.ui.tarot_session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onean.momo.data.network.exception.ServerResponseError
import com.onean.momo.data.network.repo.ai.TarotAiRepo
import com.onean.momo.data.network.repo.ai.TarotSessionTellerAction
import com.onean.momo.data.network.response.TarotCardDetail
import com.onean.momo.data.network.response.TarotTellerResponse
import com.onean.momo.ext.defaultExceptionHandler
import com.onean.momo.ui.UiError
import com.onean.momo.ui.draw_card.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class TarotSessionViewModel @Inject constructor(
    private val tarotAiRepo: TarotAiRepo,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TarotSessionUiState(step = TarotSessionStep.SetupTopic)
    )
    val uiState = _uiState.asStateFlow()
    private val _navigation = Channel<TarotSessionNavigation>()
    val navigation = _navigation.receiveAsFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.w(throwable)
        val error = when (throwable) {
            is ServerResponseError -> {
                if (throwable.code == TarotAiRepo.SESSION_NOT_FOUND_CODE) {
                    UiError.SessionNotFoundError
                } else {
                    UiError.ServerResponseError(throwable.message)
                }
            }

            else -> {
                UiError.NetworkError
            }
        }
        _uiState.update { it.copy(error = error, loading = false) }
    }

    init {
        viewModelScope.launch(exceptionHandler) {
            tarotAiRepo.startChat()
        }
    }

    fun onTopicSelected(topic: String) {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(loading = true) }
            val response = tarotAiRepo.setTopic(topic)
            _uiState.update { it.copy(loading = false) }
            handleResponse(response)
        }
    }

    fun onQuestionReply(chat: String) {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(loading = true) }
            val response = tarotAiRepo.provideDetail(chat)
            _uiState.update { it.copy(loading = false) }
            handleResponse(response)
        }
    }

    fun onCardDraw() {
        viewModelScope.launch(exceptionHandler) {
            val currentCardIndex = (_uiState.value.step as? TarotSessionStep.DrawAllKnownCards)?.nextCardIndex
                ?: error("Invalid step")
            val cardIndex = currentCardIndex + 1
            _uiState.update { it.copy(step = TarotSessionStep.DrawAllKnownCards(nextCardIndex = cardIndex)) }
        }
    }

    fun onErrorDismiss() {
        if (_uiState.value.error is UiError.SessionNotFoundError) {
            _navigation.trySend(TarotSessionNavigation.Opening)
        }
        _uiState.update { it.copy(error = null) }
    }

    private fun handleResponse(response: TarotTellerResponse) {
        when (response.action) {
            TarotSessionTellerAction.ASK_DETAIL.action -> {
                val chat = response.chat ?: return
                _uiState.update {
                    it.copy(step = TarotSessionStep.ReplyQuestion, tellerChat = chat)
                }
            }

            TarotSessionTellerAction.EXPLAIN_ALL_CARDS_AND_ASK_TO_END_GAME.action -> {
                _uiState.update {
                    it.copy(
                        step = TarotSessionStep.DrawAllKnownCards(nextCardIndex = -1),
                        drawnCardList = response.drawnTarotCardList?.map(TarotCardDetail::toUiModel)?.toImmutableList()
                            ?: persistentListOf()
                    )
                }
            }

            else -> {
                Timber.e("Unknown action: ${response.action}")
            }
        }
    }

    fun onEndSession() {
        viewModelScope.launch(exceptionHandler) {
            tarotAiRepo.endSession()
            _navigation.send(TarotSessionNavigation.Opening)
        }
    }

    fun onBeGoodBoyClick() {
        viewModelScope.launch(defaultExceptionHandler) {
            _uiState.update { it.copy(step = TarotSessionStep.ReplyQuestion) }
        }
    }
}
