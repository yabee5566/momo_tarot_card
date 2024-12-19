package com.onean.momo.ui.tarot_session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onean.momo.data.network.repo.ai.TarotAiRepo
import com.onean.momo.data.network.repo.ai.TarotSessionTellerAction
import com.onean.momo.data.network.response.TarotTellerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class TarotSessionViewModel @Inject constructor(
    private val tarotAiRepo: TarotAiRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TarotSessionUiState(
            tellerChat = "請選擇你要算的主題",
            topicList = persistentListOf("愛情", "事業", "健康"),
            step = TarotSessionStep.SetupTopic
        )
    )
    val uiState = _uiState.asStateFlow()
    private val _navigation = Channel<TarotSessionNavigation>()
    val navigation = _navigation.receiveAsFlow()

    init {
        tarotAiRepo.startChat()
    }

    // FIXME: handle all exception and show a alert dialog

    fun onTopicSelected(topic: String) {
        viewModelScope.launch {
            val response = tarotAiRepo.setupQuestionCategory(topic)
            handleResponse(response)
        }
    }

    fun onQuestionReply(chat: String) {
        viewModelScope.launch {
            val response = tarotAiRepo.replyQuestion(chat)
            handleResponse(response)
        }
    }

    fun onCardDraw() {
        viewModelScope.launch {
            val currentCardIndex = (_uiState.value.step as? TarotSessionStep.DrawAllKnownCards)?.nextCardIndex
                ?: error("Invalid step")
            val cardIndex = currentCardIndex + 1
            _uiState.update { it.copy(step = TarotSessionStep.DrawAllKnownCards(nextCardIndex = cardIndex)) }
        }
    }

    private fun handleResponse(response: TarotTellerResponse) {
        when (response.action) {
            TarotSessionTellerAction.ASK_FURTHER_QUESTION.action -> {
                val chat = response.chat ?: return
                _uiState.update {
                    it.copy(step = TarotSessionStep.ReplyQuestion, tellerChat = chat)
                }
            }

            TarotSessionTellerAction.EXPLAIN_ALL_CARDS_AND_ASK_TO_END_GAME.action -> {
                _uiState.update {
                    it.copy(
                        step = TarotSessionStep.DrawAllKnownCards(nextCardIndex = -1),
                        drawCardDetailList = response.drawnTarotCardList?.toImmutableList() ?: persistentListOf()
                    )
                }
            }

            TarotSessionTellerAction.TERMINATE.action -> {
                val chat = response.chat ?: return
                _uiState.update {
                    it.copy(step = TarotSessionStep.Terminated, tellerChat = chat)
                }
            }

            else -> {
                val chat = response.chat ?: return
                _uiState.update {
                    it.copy(step = TarotSessionStep.Error, tellerChat = chat)
                }
            }
        }
    }

    fun onEndSession() {
        viewModelScope.launch {
            Timber.d("End session")
            tarotAiRepo.endSession()
            _navigation.send(TarotSessionNavigation.Opening)
        }
    }

    fun onBeGoodBoyClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(step = TarotSessionStep.ReplyQuestion) }
        }
    }
}
