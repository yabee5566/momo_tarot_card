package com.onean.momo.ui.tarot_session

import com.onean.momo.ui.draw_card.model.DrawnTarotCardUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TarotSessionUiState(
    val tellerChat: String,
    val loading: Boolean = false,
    val step: TarotSessionStep,
    val topicList: ImmutableList<String>,
    val drawnCardList: ImmutableList<DrawnTarotCardUiModel> = persistentListOf()
)

sealed interface TarotSessionStep {
    data object SetupTopic : TarotSessionStep
    data object ReplyQuestion : TarotSessionStep
    data class DrawAllKnownCards(val nextCardIndex: Int = -1) : TarotSessionStep
    data object Error : TarotSessionStep
    data object Terminated : TarotSessionStep
}

sealed interface TarotSessionUiAction {
    data class SetupTopic(val topic: String) : TarotSessionUiAction
    data class ReplyQuestion(val chat: String) : TarotSessionUiAction
    data object OnCardDraw : TarotSessionUiAction
    data object EndSession : TarotSessionUiAction
    data object BeGoodBoyClick : TarotSessionUiAction
}

sealed interface TarotSessionNavigation {
    data object Opening : TarotSessionNavigation
}
