package com.onean.momo.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.onean.momo.ui.theme.DustYellow20
import com.onean.momo.ui.theme.Sand

@Composable
fun Loading(
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(modifier = modifier) {
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp),
                color = Sand,
                trackColor = DustYellow20,
                strokeWidth = 8.dp
            )
        }
    }
}
