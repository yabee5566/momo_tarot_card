/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onean.momo.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.onean.momo.ext.CollectFlowWithLifecycle
import com.onean.momo.ui.draw_card.DrawCardScreen
import com.onean.momo.ui.taro_question_input.TaroQuestionInputDest
import com.onean.momo.ui.taro_question_input.TaroQuestionInputScreen
import com.onean.momo.ui.taro_question_input.TaroQuestionInputViewModel
import com.onean.momo.ui.taro_question_input.TaroQuestionInputViewModelFactory
import com.onean.momo.ui.tarot_opening.TarotOpeningScreen
import com.onean.momo.ui.tarot_topic_select.TarotTopicSelectScreen
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "tarot_opening") {
        composable("tarot_opening") {
            TarotOpeningScreen(
                modifier = Modifier,
                onStartTarotClick = {
                    navController.navigate("tarot_topic_select")
                }
            )
        }
        composable("tarot_topic_select") {
            TarotTopicSelectScreen(
                modifier = Modifier.padding(16.dp),
                topicList = listOf("Love", "Career", "Health").toImmutableList(),
                onTopicSelected = { topic ->
                    navController.navigate(TaroQuestionInput(topic))
                }
            )
        }

        composable<TaroQuestionInput> { backStackEntry ->
            val topic = backStackEntry.toRoute<TaroQuestionInput>().topic
            val viewModel: TaroQuestionInputViewModel = hiltViewModel(
                creationCallback = { factory: TaroQuestionInputViewModelFactory ->
                    factory.create(topic)
                }
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CollectFlowWithLifecycle(
                flow = { viewModel.navEvent },
                onEvent = { event ->
                    when (event) {
                        is TaroQuestionInputDest.DrawCardScreen -> {
                            navController.navigate(
                                DrawCard(
                                    topic = topic,
                                    questionText = event.questionText
                                )
                            )
                        }
                    }
                }
            )

            TaroQuestionInputScreen(
                modifier = Modifier,
                uiState = uiState,
                onQuestionSubmit = viewModel::onQuestionSubmit
            )
        }
        composable<DrawCard> { backStackEntry ->
            val params = backStackEntry.toRoute<DrawCard>()
            val topic = params.topic
            val questionText = params.questionText
            DrawCardScreen(
                modifier = Modifier,
                topic = topic,
                questionText = questionText,
                onCardDraw = {
                    // FIXME: fill in here
                }
            )
        }
    }
}

@Serializable
data class TaroQuestionInput(val topic: String)

@Serializable
data class DrawCard(val topic: String, val questionText: String)
