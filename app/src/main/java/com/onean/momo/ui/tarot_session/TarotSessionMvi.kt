package com.onean.momo.ui.tarot_session

import android.content.Context
import com.onean.momo.R
import com.onean.momo.ui.UiError
import com.onean.momo.ui.draw_card.model.DrawnTarotCardUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TarotSessionUiState(
    val tellerChat: String = "",
    val loading: Boolean = false,
    val error: UiError? = null,
    val step: TarotSessionStep,
    val topicStringResList: ImmutableList<Int> =
        persistentListOf(R.string.love, R.string.career, R.string.finance, R.string.health),
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

fun UiError.toDialogContent(context: Context): String {
    return when (this) {
        is UiError.ServerResponseError -> message.ifEmpty { context.getString(R.string.server_error_please_retry) }
        UiError.NetworkError -> context.getString(R.string.server_error_please_retry)
        UiError.SessionNotFoundError -> context.getString(R.string.telling_record_missing_try_again)
    }
}
