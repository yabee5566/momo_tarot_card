package com.onean.momo.ui.ext

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.onean.momo.R
import com.onean.momo.ext.SimpleImage
import com.onean.momo.ext.safeClickable

@Composable
fun FlipSideAnim(
    frontSide: @Composable () -> Unit,
    backSide: @Composable () -> Unit,
    isOpen: Boolean,
    onOpenClick: () -> Unit,
    modifier: Modifier = Modifier,
    onFoldClick: () -> Unit,
) {
    val angle by animateFloatAsState(
        targetValue = if (isOpen) 180F else 0F,
        animationSpec = tween(1000),
        label = "rotation"
    )

    Box(modifier = modifier
        .safeClickable(
            onClick = if (isOpen) {
                onFoldClick
            } else {
                onOpenClick
            }
        )
        .graphicsLayer {
            rotationY = angle
        }
    ) {
        val isFront = (angle <= 90)
        if (isFront) {
            frontSide()
        } else {
            backSide()
        }
    }
}

@Preview
@Composable
private fun FlipSideAnimPreview() {
    Box(Modifier.fillMaxSize()) {
        var isOpen by remember { mutableStateOf(false) }
        FlipSideAnim(
            frontSide = {
                SimpleImage(
                    modifier = Modifier.size(200.dp),
                    id = R.drawable.dummy_card_back
                )
            },
            backSide = {
                SimpleImage(
                    modifier = Modifier.size(200.dp),
                    id = R.drawable.purple_star
                )
            },
            isOpen = isOpen,
            onOpenClick = { isOpen = true },
            onFoldClick = { isOpen = false }
        )
    }
}

@Preview
@Composable
private fun FlipSideAnimWithMovingPreview() {
    var isOpen by remember { mutableStateOf(false) }
    val offset by animateIntOffsetAsState(
        targetValue = if (isOpen) {
            IntOffset(20, 450)
        } else {
            IntOffset(0, 0)
        },
        animationSpec = tween(2000)
    )
    Box(modifier = Modifier.fillMaxSize()) {
        FlipSideAnim(
            modifier = Modifier
                .size(200.dp)
                .offset { offset },
            frontSide = {
                SimpleImage(
                    modifier = Modifier.size(200.dp),
                    id = R.drawable.dummy_card_back
                )
            },
            backSide = {
                SimpleImage(
                    modifier = Modifier.size(200.dp),
                    id = R.drawable.purple_star
                )
            },
            isOpen = isOpen,
            onOpenClick = { isOpen = true },
            onFoldClick = { isOpen = false }
        )
    }
}
