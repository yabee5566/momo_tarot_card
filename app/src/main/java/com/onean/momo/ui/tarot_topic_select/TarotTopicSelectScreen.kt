package com.onean.momo.ui.tarot_topic_select

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TarotTopicSelectScreen(
    topicList: ImmutableList<String>,
    onTopicSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(Color.White), horizontalAlignment = CenterHorizontally) {
        Spacer(modifier = Modifier.height(42.dp))
        Text(
            text = "What topic do you want to know about?",
            fontSize = 20.sp,
            color = Color.Magenta,
            fontWeight = FontWeight.SemiBold
        )
        LazyColumn(
            modifier = Modifier.padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(topicList.size) { index ->
                Text(
                    modifier = Modifier
                        .clickable {
                            onTopicSelected(topicList[index])
                        }
                        .width(100.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    text = topicList[index],
                    fontSize = 22.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Preview
@Composable
private fun TarotTopicSelectScreenPreview() {
    TarotTopicSelectScreen(
        topicList = listOf("Love", "Career", "Health").toImmutableList(),
        onTopicSelected = {}
    )
}