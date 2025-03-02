package com.onean.momo.ui.draw_card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onean.momo.R
import com.onean.momo.ext.SimpleImage
import com.onean.momo.ext.conditional
import com.onean.momo.ext.safeClickable
import com.onean.momo.ui.component.TarotButton
import com.onean.momo.ui.draw_card.model.DrawnTarotCardUiModel
import com.onean.momo.ui.ext.FlipSideAnim
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay

@Composable
fun DrawCardScreen(
    onCardDraw: () -> Unit,
    drawnCardUiModelList: ImmutableList<DrawnTarotCardUiModel>,
    onSayByeBye: () -> Unit,
    modifier: Modifier = Modifier,
    dummyCardCount: Int = 12
) {
    Box(modifier = modifier) {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val drawnCardWidth = remember { (screenWidth.dp - CARD_BOARD_PADDING * 2) / 3 }
        val drawnCardHeight = remember { drawnCardWidth / CARD_ASPECT_RATIO }
        val dummyCardWidth = remember { screenWidth.dp / dummyCardCount + OVERLAP_OFFSET }
        val dummyCardHeight = remember { dummyCardWidth / CARD_ASPECT_RATIO }

        var dummyCardList by remember {
            mutableStateOf(
                List(dummyCardCount) { it }.toImmutableList()
            )
        }
        var chosenCardIdList: ImmutableList<Int> by remember { mutableStateOf(persistentListOf()) }
        LazyVerticalGrid(
            // set so that the view would not bounce after draw card
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            columns = GridCells.Fixed(dummyCardCount),
            horizontalArrangement = Arrangement.spacedBy(-OVERLAP_OFFSET),
            verticalArrangement = Arrangement.Bottom
        ) {
            itemsIndexed(
                items = chosenCardIdList,
                key = { _, item -> item },
                span = { _, _ ->
                    GridItemSpan(currentLineSpan = (dummyCardCount / chosenCardIdList.size))
                },
            ) { index, _ ->
                Column(
                    modifier = Modifier.animateItem(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var isOpen by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(400)
                        isOpen = true
                    }
                    val drawnCardUiModel = drawnCardUiModelList.getOrNull(index)
                    require(drawnCardUiModel != null) {
                        "Drawn tarot card list is not enough"
                    }
                    FlipSideAnim(
                        clickable = false,
                        isOpen = isOpen,
                        frontSide = {
                            SimpleImage(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .size(width = drawnCardWidth, height = drawnCardHeight)
                                    .graphicsLayer { rotationY = 180F }
                                    .conditional(!drawnCardUiModel.isCardUpright) {
                                        rotate(degrees = 180F)
                                    }, // FIXME: this should be handled in FlipSide
                                id = drawnCardUiModel.cardDrawableId,
                                contentScale = ContentScale.FillBounds
                            )
                        },
                        backSide = {
                            SimpleImage(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .size(width = drawnCardWidth, height = drawnCardHeight),
                                id = R.drawable.card_back,
                                contentScale = ContentScale.FillBounds
                            )
                        },
                    )
                }
            }
            item(
                key = "end_session_btn",
                span = { GridItemSpan(dummyCardCount) }
            ) {
                val isLastCard by remember(chosenCardIdList) {
                    mutableStateOf(chosenCardIdList.size == 3)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = END_SESSION_BTN_PADDING)
                ) {
                    TarotButton(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .alpha(if (isLastCard) 1F else 0F),
                        text = "結束占卜",
                        onClick = onSayByeBye
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
                        .size(width = dummyCardWidth, height = dummyCardHeight)
                        .animateItem(),
                    id = R.drawable.card_back,
                    contentScale = ContentScale.FillBounds
                )
            }
        }
        LaunchedEffect(Unit) {
            repeat(3) {
                dummyCardList = dummyCardList.toMutableList().shuffled().toImmutableList()
                delay(400)
            }
        }
    }
}

private const val CARD_ASPECT_RATIO = 120 / 205F
private val OVERLAP_OFFSET = 28.dp
private val CARD_BOARD_PADDING = 16.dp
private val END_SESSION_BTN_PADDING = 16.dp

@Preview
@Composable
private fun DrawCardScreenPreview() {
    val drawnCardDrawableIdList = persistentListOf(
        DrawnTarotCardUiModel(
            cardDrawableId = R.drawable.fool_0,
            isCardUpright = false,
            cardNameWithDirection = "愚者逆位",
            answerFromCard = "愚者正位"
        ),
        DrawnTarotCardUiModel(
            cardDrawableId = R.drawable.magician_1,
            isCardUpright = true,
            cardNameWithDirection = "魔術師正位",
            answerFromCard = "魔術師正位"
        ),
        DrawnTarotCardUiModel(
            cardDrawableId = R.drawable.high_priestess_2,
            isCardUpright = true,
            cardNameWithDirection = "女教皇正位",
            answerFromCard = "女教皇正位"
        )
    )

    DrawCardScreen(
        modifier = Modifier.fillMaxSize(),
        onCardDraw = {},
        drawnCardUiModelList = drawnCardDrawableIdList,
        onSayByeBye = {}
    )
}
