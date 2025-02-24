package com.onean.momo.ui.tarot_session

import com.onean.momo.ui.UiError
import com.onean.momo.ui.draw_card.model.DrawnTarotCardUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TarotSessionUiState(
    val tellerChat: String,
    val loading: Boolean = false,
    val error: UiError? = null,
    val step: TarotSessionStep,
    val topicList: ImmutableList<String>,
    val drawnCardList: ImmutableList<DrawnTarotCardUiModel> = persistentListOf()
)

sealed interface TarotSessionStep {
    data object SetupTopic : TarotSessionStep
    data object ReplyQuestion : TarotSessionStep
    data class DrawAllKnownCards(val nextCardIndex: Int = -1) : TarotSessionStep
}

sealed interface TarotSessionUiAction {
    data class SetupTopic(val topic: String) : TarotSessionUiAction
    data class ReplyQuestion(val chat: String) : TarotSessionUiAction
    data object OnCardDraw : TarotSessionUiAction
    data object EndSession : TarotSessionUiAction
    data object BeGoodBoyClick : TarotSessionUiAction
    data object OnErrorDismiss : TarotSessionUiAction
}

sealed interface TarotSessionNavigation {
    data object Opening : TarotSessionNavigation
}

fun UiError.toDialogContent(): String {
    return when (this) {
        is UiError.ServerResponseError -> message.ifEmpty { "伺服器錯誤，請稍後再試" }
        UiError.NetworkError -> "伺服器錯誤，請稍後再試"
        UiError.SessionNotFoundError -> "占卜紀錄遺失，請重新開始"
    }
}
