package com.onean.momo.ui.draw_card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onean.momo.R
import com.onean.momo.ext.SimpleImage
import com.onean.momo.ext.safeClickable
import com.onean.momo.ui.ext.FlipSideAnim
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DrawCardScreen(
    drawnCardDrawableIdList: ImmutableList<Int>,
    modifier: Modifier = Modifier,
    dummyCardCount: Int = 24
) {
    Box(modifier = modifier) {
        val cardSize = 100.dp
        var dummyCardList by remember {
            mutableStateOf(
                List(dummyCardCount) { it }.toImmutableList()
            )
        }
        var chosenCardIdList: ImmutableList<Int> by remember { mutableStateOf(persistentListOf()) }

        LazyVerticalGrid(
            modifier = Modifier
                .background(Color.Yellow)
                .height(cardSize * 3)
                .align(Alignment.BottomCenter),
            columns = GridCells.Fixed(dummyCardCount),
            horizontalArrangement = Arrangement.spacedBy((-34).dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            itemsIndexed(
                items = chosenCardIdList,
                key = { _, item -> item },
                span = { _, _ ->
                    GridItemSpan(
                        dummyCardCount / chosenCardIdList.size
                    )
                }
            ) { index, _ ->
                Box(
                    modifier = Modifier
                        .height(cardSize * 2)
                        .animateItem(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    var isOpen by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(400)
                        isOpen = true
                    }
                    val drawableId = drawnCardDrawableIdList.getOrNull(index)
                    require(drawableId != null) {
                        "Drawn tarot card list is not enough"
                    }
                    FlipSideAnim(
                        modifier = Modifier,
                        clickable = false,
                        isOpen = isOpen,
                        frontSide = {
                            SimpleImage(
                                modifier = Modifier.size(cardSize),
                                id = drawableId
                            )
                        },
                        backSide = {
                            SimpleImage(
                                modifier = Modifier.size(cardSize),
                                id = R.drawable.dummy_card_back
                            )
                        },
                        onOpenClick = {},
                        onFoldClick = {}
                    )
                }
            }
            itemsIndexed(dummyCardList, key = { _, item -> item }) { index, cardId ->
                SimpleImage(
                    modifier = Modifier
                        .safeClickable {
                            if (chosenCardIdList.size >= 3) {
                                return@safeClickable
                            }

                            val chosenList = chosenCardIdList.toMutableList() + cardId
                            val dummyList = dummyCardList.toMutableList() - cardId
                            dummyCardList = dummyList.toImmutableList()
                            chosenCardIdList = chosenList.toImmutableList()
                        }
                        .size(cardSize)
                        .animateItem(),
                    id = R.drawable.dummy_card_back
                )
            }
        }
        val coroutineScope = rememberCoroutineScope()
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = {
                coroutineScope.launch {
                    repeat(3) {
                        dummyCardList = dummyCardList.toMutableList().shuffled().toImmutableList()
                        delay(400)
                    }
                }
            }
        ) {
            Text(text = "Shuffle")
        }
    }
}

sealed class DrawCardSessionState {
    data object Initial : DrawCardSessionState()
    data object Suffling : DrawCardSessionState()
    data object Suffled : DrawCardSessionState()
    data class FirstCardDrawn(val cardId: Int) : DrawCardSessionState()
    data class SecondCardDrawn(val cardId: Int) : DrawCardSessionState()
    data class ThirdCardDrawn(val cardId: Int) : DrawCardSessionState()
}

@Preview
@Composable
private fun DrawCardScreenPreview() {
    val drawnCardDrawableIdList = persistentListOf(
        R.drawable.sun_19,
        R.drawable.moon_18,
        R.drawable.cups06_41
    )
    DrawCardScreen(
        modifier = Modifier.fillMaxSize(),
        drawnCardDrawableIdList = drawnCardDrawableIdList
    )
}
