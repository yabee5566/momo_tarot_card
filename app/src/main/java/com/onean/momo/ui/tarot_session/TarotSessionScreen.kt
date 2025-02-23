package com.onean.momo.ui.tarot_session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onean.momo.R
import com.onean.momo.ext.SimpleImage
import com.onean.momo.ext.easyPadding
import com.onean.momo.ui.component.Loading
import com.onean.momo.ui.component.TarotButton
import com.onean.momo.ui.component.TipDialog
import com.onean.momo.ui.draw_card.DrawCardScreen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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
        val tellerChatWhole = if (uiState.step is TarotSessionStep.DrawAllKnownCards) {
            uiState.drawnCardList.getOrNull(uiState.step.nextCardIndex)?.answerFromCard ?: ""
        } else {
            uiState.tellerChat
        }
        var tellerChatEndIndex by remember(tellerChatWhole) { mutableIntStateOf(0) }
        val tellerChat by remember(tellerChatWhole) {
            derivedStateOf {
                tellerChatWhole.substring(0, tellerChatEndIndex)
            }
        }
        LaunchedEffect(tellerChatWhole) {
            snapshotFlow { tellerChatWhole }
                .collect { _ ->
                    while (tellerChatEndIndex < tellerChatWhole.length) {
                        delay(100)
                        tellerChatEndIndex++
                    }
                }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .easyPadding(top = 280.dp, horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .height(116.dp)
                    .align(Alignment.CenterHorizontally)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = tellerChat,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1F))
            when (uiState.step) {
                TarotSessionStep.SetupTopic -> {
                    ChooseTopicBlock(
                        modifier = Modifier
                            .padding(bottom = 60.dp)
                            .align(Alignment.CenterHorizontally),
                        topicList = listOf("感情", "事業", "財務", "健康").toImmutableList(),
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
                            .padding(bottom = 60.dp)
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
                        onCardDraw = {
                            onUiAction(TarotSessionUiAction.OnCardDraw)
                        },
                        drawnCardUiModelList = uiState.drawnCardList,
                        onSayByeBye = {
                            onUiAction(TarotSessionUiAction.EndSession)
                        }
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
        TipDialog(
            onDismiss = { onUiAction(TarotSessionUiAction.OnErrorDismiss) },
            onConfirmClick = { onUiAction(TarotSessionUiAction.OnErrorDismiss) },
            content = it.toDialogContent()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChooseTopicBlock(
    topicList: ImmutableList<String>,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .easyPadding(horizontal = 13.dp, top = 78.dp),
            text = "請選擇你想問的問題類型",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold
        )
        FlowRow(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            topicList.forEach { topic ->
                TarotButton(
                    text = topic,
                    onClick = { onTopicClick(topic) }
                )
            }
        }
    }
}

@Composable
private fun ReplyQuestionBlock(
    replyTextFieldState: TextFieldState,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .weight(1F),
                state = replyTextFieldState,
                textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                cursorBrush = SolidColor(Color.White),
                decorator = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (replyTextFieldState.text.isEmpty()) { // Check if text is empty
                            Text(
                                text = "輸入你的回答",
                                color = Color.Gray,
                                fontSize = 18.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Button(
                enabled = replyTextFieldState.text.isNotEmpty(),
                onClick = onSubmitClick
            ) {
                Text(text = "送出", color = Color.White)
            }
        }
    }
}

@Composable
@Preview
private fun TarotSessionScreenPreview() {
    TarotSessionScreen(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        uiState = TarotSessionUiState(
            tellerChat = "請選擇你想問的問題類型",
            topicList = persistentListOf("Love", "Career", "Health"),
            step = TarotSessionStep.SetupTopic
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
        topicList = listOf("Love", "Career", "Health").toImmutableList(),
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
