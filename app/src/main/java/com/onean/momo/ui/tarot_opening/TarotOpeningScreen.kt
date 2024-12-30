package com.onean.momo.ui.tarot_opening

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onean.momo.R
import com.onean.momo.ext.SimpleImage
import com.onean.momo.ui.theme.btnModifier

@Composable
fun TarotOpeningScreen(
    onStartTarotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        SimpleImage(
            modifier = Modifier.fillMaxSize(),
            id = R.drawable.tarot_entrance,
        )
        Text(
            modifier = Modifier
                .padding(bottom = 100.dp)
                .btnModifier()
                .clickable(onClick = onStartTarotClick)
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            text = "進行一場塔羅占卜",
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
