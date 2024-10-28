package com.onean.momo.ui.tarot_opening

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TarotOpeningScreen(
    onStartTarotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.White)) {
        Text(
            modifier = Modifier
                .padding(top = 80.dp)
                .clip(CircleShape)
                .clickable(onClick = onStartTarotClick)
                .align(Alignment.Center)
                .background(Color.Magenta)
                .padding(16.dp),
            text = "Let's start a Tarot reading!",
            color = Color.White,
            fontSize = 20.sp
        )

    }
}

@Composable
@Preview
private fun TarotOpeningScreenPreview() {
    TarotOpeningScreen(
        onStartTarotClick = {}
    )
}

