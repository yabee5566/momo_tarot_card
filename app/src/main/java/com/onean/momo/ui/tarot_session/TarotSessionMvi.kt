package com.onean.momo.ui.tarot_session

import kotlinx.collections.immutable.ImmutableList

sealed interface TarotSessionUiState {
    data class PendingTopicSetup(val tellerChat: String, val topicList: ImmutableList<String>) : TarotSessionUiState
    data class PendingReplyQuestion(val tellerChat: String) : TarotSessionUiState
    data class PendingDrawCard(val tellerChat: String) : TarotSessionUiState
    data class PendingByeBye(val tellerChat: String) : TarotSessionUiState
    data class Error(val tellerChat: String) : TarotSessionUiState
    data class Terminated(val tellerChat: String) : TarotSessionUiState
}

sealed interface TarotSessionUiAction {
    data class SetupTopic(val topic: String) : TarotSessionUiAction
    data class ReplyQuestion(val chat: String) : TarotSessionUiAction
    data object DrawCard : TarotSessionUiAction
    data object ByeBye : TarotSessionUiAction
}
