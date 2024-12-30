package com.onean.momo.ui.draw_card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onean.momo.R
import com.onean.momo.ext.SimpleImage
import com.onean.momo.ext.safeClickable
import com.onean.momo.ui.ext.FlipSideAnim
import com.onean.momo.ui.theme.btnModifier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay

@Composable
fun DrawCardScreen(
    drawnCardDrawableIdList: ImmutableList<Int>,
    onCardDraw: () -> Unit,
    onSayByeBye: () -> Unit,
    modifier: Modifier = Modifier,
    dummyCardCount: Int = 24
) {
    Box(modifier = modifier) {
        var dummyCardList by remember {
            mutableStateOf(
                List(dummyCardCount) { it }.toImmutableList()
            )
        }
        var chosenCardIdList: ImmutableList<Int> by remember { mutableStateOf(persistentListOf()) }

        LazyVerticalGrid(
            modifier = Modifier
                .height(CARD_BOARD_HEIGHT)
                .align(Alignment.BottomCenter),
            columns = GridCells.Fixed(dummyCardCount),
            horizontalArrangement = Arrangement.spacedBy((-34).dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            itemsIndexed(
                items = chosenCardIdList,
                key = { _, item -> item },
                span = { _, _ ->
                    GridItemSpan(currentLineSpan = (dummyCardCount / chosenCardIdList.size))
                }
            ) { index, _ ->
                Box(
                    modifier = Modifier
                        .height(CARD_HEIGHT * 3)
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
                                modifier = Modifier.size(width = CARD_WIDTH * 1.8F, height = CARD_HEIGHT * 1.8F),
                                id = drawableId
                            )
                        },
                        backSide = {
                            SimpleImage(
                                modifier = Modifier.size(width = CARD_WIDTH * 1.8F, height = CARD_HEIGHT * 1.8F),
                                id = R.drawable.dummy_card_back
                            )
                        },
                        onOpenClick = {},
                        onFoldClick = {}
                    )
                }
            }
            itemsIndexed(items = dummyCardList, key = { _, item -> item }) { index, cardId ->
                SimpleImage(
                    modifier = Modifier
                        .safeClickable {
                            if (chosenCardIdList.size >= 3) {
                                return@safeClickable
                            }
                            onCardDraw()
                            val chosenList = chosenCardIdList.toMutableList() + cardId
                            val dummyList = dummyCardList.toMutableList() - cardId
                            dummyCardList = dummyList.toImmutableList()
                            chosenCardIdList = chosenList.toImmutableList()
                        }
                        .size(width = CARD_WIDTH, height = CARD_HEIGHT)
                        .animateItem(),
                    id = R.drawable.dummy_card_back
                )
            }
        }
        LaunchedEffect(Unit) {
            repeat(3) {
                dummyCardList = dummyCardList.toMutableList().shuffled().toImmutableList()
                delay(400)
            }
        }

        val isLastCard by remember(chosenCardIdList) {
            mutableStateOf(chosenCardIdList.size == 3)
        }
        if (isLastCard) {
            Text(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .btnModifier()
                    .padding(16.dp)
                    .safeClickable(onClick = onSayByeBye)
                    .align(Alignment.Center),
                fontSize = 22.sp,
                text = "謝謝老師～～",
                color = Color.White
            )
        }
    }
}

private val CARD_HEIGHT = 106.dp
private val CARD_WIDTH = 60.dp
private val CARD_BOARD_HEIGHT = CARD_HEIGHT * 4

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
        drawnCardDrawableIdList = drawnCardDrawableIdList,
        onCardDraw = {},
        onSayByeBye = {}
    )
}
