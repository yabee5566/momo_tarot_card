package com.onean.momo.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

fun Modifier.btnModifier() = this
    .clip(RoundedCornerShape(16.dp))
        .border(width = 2.dp, color = Gold, shape = RoundedCornerShape(16.dp))
        .background(PaleWood)
