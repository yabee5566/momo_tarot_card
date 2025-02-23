package com.onean.momo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onean.momo.ext.safeClickable
import com.onean.momo.ui.theme.Gold
import com.onean.momo.ui.theme.PaleWood

@Composable
fun TarotButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(width = 2.dp, color = Gold, shape = RoundedCornerShape(16.dp))
            .background(PaleWood)
            .padding(vertical = 13.dp, horizontal = 13.dp)
            .safeClickable(onClick = onClick),
        text = text,
        color = Color.White,
        fontSize = 17.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold
    )
}

@Preview
@Composable
private fun TarotButtonPreview() {
    TarotButton(text = "進行一場塔羅占卜", onClick = {})
}
