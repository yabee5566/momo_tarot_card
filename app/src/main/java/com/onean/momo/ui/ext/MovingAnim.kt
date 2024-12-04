package com.onean.momo.ui.ext

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MovingAnim(
    pathIndexList: ImmutableList<IntOffset>,
    pathTimingList: ImmutableList<Int>,
    currentPathIndex: Int,
    onMovingFinished: (pathIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    val rememberedPathIndexList = remember { pathIndexList }
    val rememberedPathTimingList = remember { pathTimingList }
    val rememberedOnMovingFinished by rememberUpdatedState(onMovingFinished)

    val offset = remember {
        Animatable(
            rememberedPathIndexList[0], IntOffset.VectorConverter
        )
    }
    LaunchedEffect(currentPathIndex) {
        offset.animateTo(
            targetValue = rememberedPathIndexList[currentPathIndex],
            animationSpec = tween(rememberedPathTimingList[currentPathIndex])
        )
        rememberedOnMovingFinished(currentPathIndex)
    }

    Box(modifier = modifier) {
        content(Modifier.offset { offset.value })
    }
}

@Preview
@Composable
private fun MovingAnimPreview() {
    val pathIndexList = persistentListOf(
        IntOffset(0, 100),
        IntOffset(100, 100),
        IntOffset(200, 200),
        IntOffset(300, 300),
        IntOffset(0, 400)
    )
    val pathTimingList = persistentListOf(0, 300, 600, 900, 1200)
    var currentPathIndex by remember { mutableIntStateOf(0) }

    MovingAnim(
        modifier = Modifier
            .background(Color.Blue)
            .fillMaxSize(),
        pathIndexList = pathIndexList,
        pathTimingList = pathTimingList,
        currentPathIndex = currentPathIndex,
        onMovingFinished = { pathIndex ->
            if (pathIndex < pathIndexList.lastIndex) {
                currentPathIndex = pathIndex + 1
            }
        },
        content = { modifier ->
            Box(
                modifier = modifier
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Red)
                )
            }
        }
    )
}
