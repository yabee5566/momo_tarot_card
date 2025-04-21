package com.onean.momo.ui.tarot_session

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.onean.momo.R
import com.onean.momo.ext.SimpleImage
import com.onean.momo.ext.conditional
import com.onean.momo.ext.isTablet
import com.onean.momo.ext.safeClickable
import com.onean.momo.ui.component.Loading
import com.onean.momo.ui.component.TarotButton
import com.onean.momo.ui.component.TipDialog
import com.onean.momo.ui.draw_card.DrawCardScreen
import com.onean.momo.ui.theme.Dark30
import com.onean.momo.ui.theme.White50
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay

@Composable
fun TarotSessionScreen(
    uiState: TarotSessionUiState,
    onUiAction: (uiAction: TarotSessionUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        SimpleImage(
            modifier = Modifier.fillMaxSize(),
            id = R.drawable.tarot_teller_avatar,
        )
        val style = if (isTablet()) {
            TarotSessionScreenStyle.Tablet
        } else {
            TarotSessionScreenStyle.Phone
        }
        var selectedOpenCardIndex by remember { mutableIntStateOf(-1) }
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1F))
            when (uiState.step) {
                TarotSessionStep.SetupTopic -> {
                    ChooseTopicBlock(
                        modifier = Modifier
                            .padding(style.bottomActionBlockPadding)
                            .align(Alignment.CenterHorizontally),
                        topicStringResList = uiState.topicStringResList,
                        onTopicClick = {
                            onUiAction(TarotSessionUiAction.SetupTopic(it))
                        }
                    )
                }

                TarotSessionStep.ReplyQuestion -> {
                    val replyTextFieldState = rememberTextFieldState()
                    val localFocusManager = LocalFocusManager.current
                    ReplyQuestionBlock(
                        modifier = Modifier
                            .padding(style.bottomActionBlockPadding)
                            .align(Alignment.CenterHorizontally),
                        replyTextFieldState = replyTextFieldState,
                        onSubmitClick = {
                            onUiAction(TarotSessionUiAction.ReplyQuestion(replyTextFieldState.text.toString()))
                            replyTextFieldState.clearText()
                            localFocusManager.clearFocus()
                        }
                    )
                }

                is TarotSessionStep.DrawAllKnownCards -> {
                    DrawCardScreen(
                        modifier = Modifier
                            .padding(style.drawCardScreenPadding)
                            .fillMaxSize(),
                        drawnCardUiModelList = uiState.drawnCardList,
                        onSayByeBye = {
                            onUiAction(TarotSessionUiAction.EndSession)
                        },
                        selectedOpenCardIndex = selectedOpenCardIndex,
                        onSelectedOpenCardChanged = { cardIndex ->
                            selectedOpenCardIndex = cardIndex
                        }
                    )
                }
            }
        }

        val tellerChatWhole = when (uiState.step) {
            is TarotSessionStep.DrawAllKnownCards -> {
                uiState.drawnCardList.getOrNull(selectedOpenCardIndex)?.answerFromCard
                    ?: stringResource(R.string.close_eyes_and_prepare_to_draw)
            }

            is TarotSessionStep.SetupTopic -> {
                stringResource(R.string.choose_topic_hint)
            }

            else -> {
                uiState.tellerChat
            }
        }
        var tellerChatEndIndex by remember(tellerChatWhole) { mutableIntStateOf(0) }
        val displayTellerChat by remember(tellerChatWhole) {
            derivedStateOf {
                tellerChatWhole.substring(0, tellerChatEndIndex)
            }
        }
        LaunchedEffect(tellerChatWhole) {
            snapshotFlow { tellerChatWhole }
                .collect { _ ->
                    while (tellerChatEndIndex < tellerChatWhole.length) {
                        delay(85)
                        tellerChatEndIndex++
                    }
                }
        }
        var displayTellerChatLines by remember(tellerChatWhole) { mutableIntStateOf(0) }
        val displayTellerChatScrollState = rememberScrollState()
        LaunchedEffect(displayTellerChatLines) {
            displayTellerChatScrollState.animateScrollTo(Int.MAX_VALUE)
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(style.tellerChatPadding)
        ) {
            var isExpanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(Dark30)
                    .padding(8.dp)
                    .safeClickable {
                        // click on whole area would expand
                        isExpanded = !isExpanded
                    }
            ) {
                Text(
                    modifier = Modifier
                        .animateContentSize()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .conditional(isExpanded) {
                            padding(top = 100.dp)
                            fillMaxHeight(0.8F)
                        }
                        .conditional(!isExpanded) {
                            height(style.tellerChatHeight)
                        }
                        .fillMaxWidth()
                        .verticalScroll(displayTellerChatScrollState),
                    text = displayTellerChat,
                    color = Color.White,
                    fontSize = style.tellerChatFontSize,
                    style = TextStyle(lineHeight = 1.2.em),
                    fontWeight = FontWeight.SemiBold,
                    onTextLayout = { result ->
                        displayTellerChatLines = result.lineCount
                    }
                )
                val isExpandIconVisible = (uiState.step == TarotSessionStep.ReplyQuestion ||
                    uiState.step is TarotSessionStep.DrawAllKnownCards)
                if (isExpandIconVisible) {
                    val expandDownIconDegree by animateFloatAsState(
                        targetValue = if (isExpanded) 180F else 0F,
                        animationSpec = tween(800)
                    )
                    val infiniteTransition = rememberInfiniteTransition()
                    val expandDownIconAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.2F,
                        targetValue = 1F,
                        animationSpec = InfiniteRepeatableSpec(
                            animation = tween(1600),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = ""
                    )
                    SimpleImage(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .graphicsLayer {
                                alpha = expandDownIconAlpha
                                rotationX = expandDownIconDegree
                            },
                        id = R.drawable.expand_down,
                        tint = White50
                    )
                }
            }
        }
        Loading(
            modifier = Modifier.align(Alignment.Center),
            isLoading = uiState.loading
        )
    }

    uiState.error?.let {
        val context = LocalContext.current
        TipDialog(
            onDismiss = { onUiAction(TarotSessionUiAction.OnErrorDismiss) },
            onConfirmClick = { onUiAction(TarotSessionUiAction.OnErrorDismiss) },
            content = it.toDialogContent(context = context)
        )
    }
}

interface SessionScreenStyle {
    val tellerChatFontSize: TextUnit
    val tellerChatPadding: PaddingValues
    val tellerChatHeight: Dp
    val bottomActionBlockPadding: PaddingValues
    val drawCardScreenPadding: PaddingValues
}

enum class TarotSessionScreenStyle : SessionScreenStyle {
    Phone {
        override val tellerChatFontSize = 22.sp
        override val tellerChatPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 150.dp)
        override val tellerChatHeight = 58.dp
        override val bottomActionBlockPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 0.dp, bottom = 60.dp)
        override val drawCardScreenPadding = PaddingValues(20.dp)
    },
    Tablet {
        override val tellerChatFontSize = 44.sp
        override val tellerChatPadding = PaddingValues(start = 40.dp, end = 40.dp, top = 0.dp, bottom = 300.dp)
        override val tellerChatHeight = 116.dp
        override val bottomActionBlockPadding = PaddingValues(start = 64.dp, end = 64.dp, top = 0.dp, bottom = 120.dp)
        override val drawCardScreenPadding = PaddingValues(40.dp)
    },
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChooseTopicBlock(
    topicStringResList: ImmutableList<Int>,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val style = if (isTablet()) {
        TarotChooseTopicStyle.Tablet
    } else {
        TarotChooseTopicStyle.Phone
    }

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.choose_topic_hint),
            color = Color.White,
            fontSize = style.titleFontSize,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(style.separatorHeight))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(style.topicHorizontalSpacing),
        ) {
            topicStringResList.forEach { topicResId ->
                val topicText = stringResource(topicResId)
                TarotButton(
                    text = topicText,
                    onClick = { onTopicClick(topicText) }
                )
            }
        }
    }
}

interface ChooseTopicStyle {
    val titleFontSize: TextUnit
    val separatorHeight: Dp
    val topicHorizontalSpacing: Dp
}

enum class TarotChooseTopicStyle : ChooseTopicStyle {
    Phone {
        override val titleFontSize = 19.sp
        override val separatorHeight = 9.dp
        override val topicHorizontalSpacing = 9.dp
    },
    Tablet {
        override val titleFontSize = 38.sp
        override val separatorHeight = 18.dp
        override val topicHorizontalSpacing = 18.dp
    },
}

@Composable
private fun ReplyQuestionBlock(
    replyTextFieldState: TextFieldState,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val style = if (isTablet()) {
                TarotReplyQuestionStyle.Tablet
            } else {
                TarotReplyQuestionStyle.Phone
            }
            BasicTextField(
                modifier = Modifier.weight(1F),
                state = replyTextFieldState,
                textStyle = TextStyle(color = Color.White, fontSize = style.inputFontSize),
                cursorBrush = SolidColor(Color.White),
                lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 1, maxHeightInLines = 3),
                decorator = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (replyTextFieldState.text.isEmpty()) { // Check if text is empty
                            Text(
                                text = stringResource(R.string.fill_in_your_reply),
                                color = Color.Gray,
                                fontSize = style.inputFontSize
                            )
                        }
                        innerTextField()
                    }
                }
            )
            Spacer(modifier = Modifier.width(style.separatorSpacing))
            TarotButton(
                text = stringResource(R.string.submit),
                enabled = replyTextFieldState.text.isNotEmpty(),
                onClick = onSubmitClick
            )
        }
    }
}

interface ReplyQuestionStyle {
    val inputFontSize: TextUnit
    val separatorSpacing: Dp
}

enum class TarotReplyQuestionStyle : ReplyQuestionStyle {
    Phone {
        override val inputFontSize = 18.sp
        override val separatorSpacing = 8.dp
    },
    Tablet {
        override val inputFontSize = 36.sp
        override val separatorSpacing = 16.dp
    },
}

@Composable
@Preview
private fun TarotSessionScreenPreview() {
    TarotSessionScreen(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        uiState = TarotSessionUiState(
            tellerChat = "請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss請選擇您想問的問題類型ssssss",
            topicStringResList = persistentListOf(R.string.love, R.string.career, R.string.finance, R.string.health),
            step = TarotSessionStep.ReplyQuestion
        ),
        onUiAction = {},
    )
}

@Preview
@Composable
private fun ChooseTopicBlockPreview() {
    ChooseTopicBlock(
        modifier = Modifier
            .background(Color.Red)
            .fillMaxWidth(),
        topicStringResList = persistentListOf(R.string.love, R.string.career, R.string.finance, R.string.health),
        onTopicClick = {}
    )
}

@Preview
@Composable
private fun ReplyQuestionBlockPreview() {
    ReplyQuestionBlock(
        modifier = Modifier
            .background(Color.Red)
            .fillMaxWidth(),
        replyTextFieldState = TextFieldState(),
        onSubmitClick = {}
    )
}
