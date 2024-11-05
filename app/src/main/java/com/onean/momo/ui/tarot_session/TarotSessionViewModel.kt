package com.onean.momo.ui.tarot_session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onean.momo.data.network.repo.ai.TarotAiRepo
import com.onean.momo.data.network.response.TarotTellerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TarotSessionViewModel @Inject constructor(
    val tarotAiRepo: TarotAiRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow<TarotSessionUiState>(
        TarotSessionUiState.PendingTopicSetup(
            "請選擇你要算的主題",
            persistentListOf("愛情", "事業", "健康")
        )
    )
    val uiState = _uiState.asStateFlow()

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
                    TarotSessionUiState.PendingReplyQuestion(chat)
                }
            }

            "ask_to_draw_card", "explain_card_drew_and_ask_draw_next" -> {
                val chat = response.chat ?: return
                _uiState.update {
                    TarotSessionUiState.PendingDrawCard(chat)
                }
            }

            "explain_last_card_ask_to_end_game" -> {
                val chat = response.chat ?: return
                _uiState.update {
                    TarotSessionUiState.PendingByeBye(chat)
                }
            }

            "terminate" -> {
                val chat = response.chat ?: return
                _uiState.update {
                    TarotSessionUiState.Terminated(chat)
                }
            }

            else -> {
                val chat = response.chat ?: return
                _uiState.update {
                    TarotSessionUiState.Error(chat)
                }
            }
        }
    }

    fun onByeBye() {
        viewModelScope.launch {
            val response = tarotAiRepo.endSession()
            handleResponse(response)
        }
    }
}
