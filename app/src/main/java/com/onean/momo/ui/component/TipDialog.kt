package com.onean.momo.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onean.momo.ext.SimpleDialog
import com.onean.momo.ext.isTablet
import com.onean.momo.ui.theme.Dark

@Composable
fun TipDialog(
    content: String,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTablet = isTablet()
    val style = if (isTablet) {
        TipDialogStyle.Tablet
    } else {
        TipDialogStyle.Phone
    }

    SimpleDialog(
        modifier = modifier,
        onDismiss = onDismiss,
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "提示",
                color = Dark,
                fontSize = style.titleFontSize,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(style.elementDividerHeight))
            Text(
                text = content,
                color = Dark,
                fontSize = style.contentFontSize
            )
            Spacer(modifier = Modifier.height(style.elementDividerHeight))
            TarotButton(
                modifier = Modifier.width(style.buttonWidth),
                text = "確定",
                onClick = onConfirmClick
            )
        }
    }
}

private interface DialogStyle {
    val titleFontSize: TextUnit
    val contentFontSize: TextUnit
    val elementDividerHeight: Dp
    val buttonWidth: Dp
}

enum class TipDialogStyle : DialogStyle {
    Phone {
        override val titleFontSize = 19.sp
        override val contentFontSize = 19.sp
        override val elementDividerHeight = 24.dp
        override val buttonWidth = 200.dp
    },
    Tablet {
        override val titleFontSize = 38.sp
        override val contentFontSize = 38.sp
        override val elementDividerHeight = 48.dp
        override val buttonWidth = 400.dp
    }
}

@Composable
@Preview
private fun TipDialogPreview() {
    TipDialog(
        content = "這是一個提示",
        onConfirmClick = {},
        onDismiss = {}
    )
}
