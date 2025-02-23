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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onean.momo.ext.SimpleDialog
import com.onean.momo.ui.theme.Dark

@Composable
fun TipDialog(
    content: String,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = content,
                color = Dark,
                fontSize = 19.sp)
            Spacer(modifier = Modifier.height(24.dp))
            TarotButton(
                modifier = Modifier.width(200.dp),
                text = "確定",
                onClick = onConfirmClick
            )
        }
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
