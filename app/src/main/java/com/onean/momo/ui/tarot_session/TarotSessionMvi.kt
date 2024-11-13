package com.onean.momo.ui.tarot_session

import kotlinx.collections.immutable.ImmutableList

data class TarotSessionUiState(
    val tellerChat: String,
    val loading: Boolean = false,
    val step: TarotSessionStep,
    val topicList: ImmutableList<String>
)

enum class TarotSessionStep {
    SETUP_TOPIC,
    REPLY_QUESTION,
    DRAW_CARD,
    BYE_BYE,
    ERROR,
    TERMINATED
}

sealed interface TarotSessionUiAction {
    data class SetupTopic(val topic: String) : TarotSessionUiAction
    data class ReplyQuestion(val chat: String) : TarotSessionUiAction
    data object DrawCard : TarotSessionUiAction
    data object EndSession : TarotSessionUiAction
    data object BeGoodBoyClick : TarotSessionUiAction
}

sealed interface TarotSessionNavigation {
    data object Opening : TarotSessionNavigation
}
