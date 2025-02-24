package com.onean.momo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
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
import com.onean.momo.ext.conditional
import com.onean.momo.ext.safeClickable
import com.onean.momo.ui.theme.Gold
import com.onean.momo.ui.theme.Gold50
import com.onean.momo.ui.theme.PaleWood
import com.onean.momo.ui.theme.PaleWood50
import com.onean.momo.ui.theme.White50

@Composable
fun TarotButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .conditional(enabled) {
                safeClickable(onClick = onClick)
            }
            .border(
                width = 2.dp,
                color = if (enabled) Gold else Gold50,
                shape = RoundedCornerShape(20.dp)
            )
            .background(if (enabled) PaleWood else PaleWood50)
            .padding(vertical = 13.dp, horizontal = 13.dp),
        text = text,
        color = if (enabled) Color.White else White50,
        fontSize = 17.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold
    )
}

@Preview
@Composable
private fun TarotButtonPreview() {
    Column {
        TarotButton(text = "Go", enabled = true, onClick = {})
        TarotButton(text = "Go", enabled = false, onClick = {})
    }
}
