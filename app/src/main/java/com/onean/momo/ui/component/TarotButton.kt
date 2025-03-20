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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onean.momo.ext.conditional
import com.onean.momo.ext.isTablet
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
    val isTablet = isTablet()
    val style = if (isTablet) {
        TarotButtonStyle.Tablet
    } else {
        TarotButtonStyle.Phone
    }

    Text(
        modifier = modifier
            .clip(RoundedCornerShape(style.cornerRadius))
            .conditional(enabled) {
                safeClickable(onClick = onClick)
            }
            .border(
                width = style.borderWidth,
                color = if (enabled) Gold else Gold50,
                shape = RoundedCornerShape(style.cornerRadius)
            )
            .background(if (enabled) PaleWood else PaleWood50)
            .padding(style.padding),
        text = text,
        color = if (enabled) Color.White else White50,
        fontSize = style.fontSize,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold
    )
}

private interface ButtonStyle {
    val cornerRadius: Dp
    val borderWidth: Dp
    val padding: Dp
    val fontSize: TextUnit
}

enum class TarotButtonStyle : ButtonStyle {
    Phone {
        override val cornerRadius = 20.dp
        override val borderWidth = 2.dp
        override val padding: Dp = 13.dp
        override val fontSize: TextUnit = 17.sp
    },
    Tablet {
        override val cornerRadius = 40.dp
        override val borderWidth = 4.dp
        override val padding: Dp = 26.dp
        override val fontSize: TextUnit = 34.sp
    },
}

@Preview
@Composable
private fun TarotButtonPreview() {
    Column {
        TarotButton(text = "Go", enabled = true, onClick = {})
        TarotButton(text = "Go", enabled = false, onClick = {})
    }
}
