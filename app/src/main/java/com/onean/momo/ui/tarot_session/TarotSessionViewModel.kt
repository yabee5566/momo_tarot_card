package com.onean.momo.ui.tarot_session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onean.momo.data.network.repo.ai.TarotAiRepo
import com.onean.momo.data.network.response.TarotTellerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TarotSessionViewModel @Inject constructor(
    private val tarotAiRepo: TarotAiRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TarotSessionUiState(
            tellerChat = "請選擇你要算的主題",
            topicList = persistentListOf("愛情", "事業", "健康"),
            step = TarotSessionStep.SETUP_TOPIC
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

    fun onDrawCard() {
        viewModelScope.launch {
            val response = tarotAiRepo.drawCard()
            handleResponse(response)
        }
    }

    private fun handleResponse(response: TarotTellerResponse) {
        when (response.action) {
            "ask_further_question" -> {
                val chat = response.chat ?: return
                _uiState.update {
                    it.copy(step = TarotSessionStep.REPLY_QUESTION, tellerChat = chat)
                }
            }

            "ask_to_draw_card", "explain_card_drew_and_ask_draw_next" -> {
                val chat = response.chat ?: return
                _uiState.update {
                    it.copy(step = TarotSessionStep.DRAW_CARD, tellerChat = chat)
                }
            }

            "explain_last_card_ask_to_end_game" -> {
                val chat = response.chat ?: return
                _uiState.update {
                    it.copy(step = TarotSessionStep.BYE_BYE, tellerChat = chat)
                }
            }

            "terminate" -> {
                val chat = response.chat ?: return
                _uiState.update {
                    it.copy(step = TarotSessionStep.TERMINATED, tellerChat = chat)
                }
            }

            else -> {
                val chat = response.chat ?: return
                _uiState.update {
                    it.copy(step = TarotSessionStep.ERROR, tellerChat = chat)
                }
            }
        }
    }

    fun onEndSession() {
        viewModelScope.launch {
            tarotAiRepo.endSession()
            _navigation.send(TarotSessionNavigation.Opening)
        }
    }

    fun onBeGoodBoyClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(step = TarotSessionStep.REPLY_QUESTION) }
        }
    }
}
