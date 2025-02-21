package com.onean.momo.ui.tarot_opening

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onean.momo.R
import com.onean.momo.ext.SimpleImage
import com.onean.momo.ui.component.TarotButton

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
        TarotButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
            text = "進行一場塔羅占卜",
            onClick = onStartTarotClick
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
