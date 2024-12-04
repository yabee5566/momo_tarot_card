package com.onean.momo.ui.tarot_session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onean.momo.R
import com.onean.momo.ext.SimpleImage
import com.onean.momo.ext.safeClickable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TarotSessionScreen(
    uiState: TarotSessionUiState,
    onUiAction: (uiAction: TarotSessionUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        SimpleImage(
            modifier = Modifier.fillMaxSize(),
            id = R.drawable.purple_star
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 13.dp)
                .align(Alignment.Center),
            text = uiState.tellerChat,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold
        )
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            when (uiState.step) {
                TarotSessionStep.SETUP_TOPIC -> {
                    ChooseTopicBlock(
                        topicList = listOf("Love", "Career", "Health").toImmutableList(),
                        onTopicClick = {
                            onUiAction(TarotSessionUiAction.SetupTopic(it))
                        }
                    )
                }

                TarotSessionStep.REPLY_QUESTION -> {
                    val replyTextFieldState = rememberTextFieldState()
                    ReplyQuestionBlock(
                        replyTextFieldState = replyTextFieldState,
                        onSubmitClick = {
                            onUiAction(TarotSessionUiAction.ReplyQuestion(replyTextFieldState.text.toString()))
                            replyTextFieldState.clearText()
                        }
                    )
                }

                TarotSessionStep.DRAW_CARD -> {
                    DrawCardBlock(
                        onDrawCardClick = {
                            onUiAction(TarotSessionUiAction.DrawCard)
                        }
                    )
                }

                TarotSessionStep.BYE_BYE -> {
                    ActionButtonBlock(
                        actionText = "Bye Bye",
                        onActionClick = {
                            onUiAction(TarotSessionUiAction.EndSession)
                        }
                    )
                }

                TarotSessionStep.ERROR -> {
                    ActionButtonBlock(
                        actionText = "好啦！",
                        onActionClick = {
                            onUiAction(TarotSessionUiAction.BeGoodBoyClick)
                        }
                    )
                }

                TarotSessionStep.TERMINATED -> {
                    ActionButtonBlock(
                        actionText = "結束",
                        onActionClick = {
                            onUiAction(TarotSessionUiAction.EndSession)
                        }
                    )
                }
            }
        }
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
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            text = "請選擇你想問的問題類型",
            color = Color.White,
            fontSize = 18.sp
        )
        FlowRow(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            topicList.forEach { topic ->
                Text(
                    modifier = Modifier
                        .chipModifier()
                        .safeClickable { onTopicClick(topic) },
                    text = topic,
                    fontSize = 22.sp
                )
            }
        }
    }
}

private fun Modifier.chipModifier(): Modifier = this
    .clip(CircleShape)
    .background(Color.Gray)
    .padding(horizontal = 4.dp, vertical = 2.dp)

@Composable
private fun ReplyQuestionBlock(
    replyTextFieldState: TextFieldState,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "請回答老師的問題：",
            color = Color.White,
            fontSize = 18.sp
        )
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
                                text = "Enter your question here",
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
                Text(text = "Submit")
            }
        }
    }
}

@Composable
private fun DrawCardBlock(
    onDrawCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .chipModifier()
            .safeClickable(onClick = onDrawCardClick),
        text = "抽一張塔羅牌",
        fontSize = 22.sp,
    )
}

@Composable
private fun ActionButtonBlock(
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .chipModifier()
            .safeClickable(onClick = onActionClick),
        text = actionText,
        fontSize = 22.sp
    )
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
            step = TarotSessionStep.SETUP_TOPIC
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

@Preview
@Composable
private fun DrawCardBlockPreview() {
    DrawCardBlock(onDrawCardClick = {})
}
