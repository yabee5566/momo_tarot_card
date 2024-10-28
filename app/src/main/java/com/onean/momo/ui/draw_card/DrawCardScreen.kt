package com.onean.momo.ui.draw_card

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DrawCardScreen(
    topic: String,
    questionText: String,
    onCardDraw: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column{
        // FIXME: fill in here
    }
}

@Preview
@Composable
fun DrawCardScreenPreview() {
    DrawCardScreen(
        topic = "career",
        questionText = "should i quit my job",
        onCardDraw = {}
    )
}