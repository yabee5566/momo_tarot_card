package com.onean.momo.ui.ext

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
        val originList = List(12) {
            IntOffset(100 * it, 1200)
        }
        var openCardIndexList: List<Int> by remember { mutableStateOf(emptyList()) }
        val openCardCount = openCardIndexList.size

        originList.forEachIndexed { index, origin ->
            val openIndex by remember { derivedStateOf { openCardIndexList.indexOf(index) } }
            var isOpen by remember { mutableStateOf(false) }
            LaunchedEffect(isOpen) {
                if (isOpen) {
                    openCardIndexList = openCardIndexList + index
                } else {
                    openCardIndexList = openCardIndexList - index
                }
            }

            val offset by animateIntOffsetAsState(
                targetValue = if (isOpen) IntOffset(20 + 200 * openIndex, 450) else origin,
                animationSpec = tween(2000)
            )
            val sizeRatio by animateFloatAsState(
                targetValue = if (isOpen) 0.3F else 1F,
                animationSpec = tween(2000)
            )
            val cardSize = (200*sizeRatio).dp
            FlipSideAnim(
                modifier = Modifier
                    .size(cardSize)
                    .offset { offset },
                isOpen = isOpen,
                frontSide = {
                    SimpleImage(
                        modifier = Modifier.size(cardSize),
                        id = R.drawable.dummy_card_back
                    )
                },
                backSide = {
                    SimpleImage(
                        modifier = Modifier.size(cardSize),
                        id = R.drawable.purple_star
                    )

                },
                onOpenClick = { isOpen = true },
                onFoldClick = { isOpen = false }
            )
        }
    }
}