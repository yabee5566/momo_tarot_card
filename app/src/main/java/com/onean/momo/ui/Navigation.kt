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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.onean.momo.ext.CollectFlowWithLifecycle
import com.onean.momo.ext.localActivity
import com.onean.momo.ui.tarot_opening.TarotOpeningScreen
import com.onean.momo.ui.tarot_session.TarotSessionNavigation
import com.onean.momo.ui.tarot_session.TarotSessionScreen
import com.onean.momo.ui.tarot_session.TarotSessionUiAction
import com.onean.momo.ui.tarot_session.TarotSessionViewModel
import timber.log.Timber

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "tarot_opening") {
        composable("tarot_opening") {
            TarotOpeningScreen(
                modifier = Modifier,
                onStartTarotClick = {
                    navController.navigate("tarot_session")
                }
            )
        }
        composable("tarot_session") {
            val viewModel: TarotSessionViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val adRequest = AdRequest.Builder().build()
            val activity = localActivity()
            var interstitialAd: InterstitialAd? by remember { mutableStateOf(null) }
            LaunchedEffect(Unit) {
                InterstitialAd.load(activity, "ca-app-pub-3940256099942544/1033173712", adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Timber.d(adError.toString())
                            interstitialAd = null
                        }

                        override fun onAdLoaded(ad: InterstitialAd) {
                            Timber.d("Ad was loaded.")
                            interstitialAd = ad
                        }
                    }
                )
            }
            CollectFlowWithLifecycle(viewModel::navigation) {
                when (it) {
                    TarotSessionNavigation.Opening -> {
                        navController.popBackStack() // back to tarot_opening
                        interstitialAd?.show(activity)
                    }
                }
            }
            TarotSessionScreen(
                uiState = uiState,
                onUiAction = {
                    Timber.d("onUiAction: $it")
                    when (it) {
                        is TarotSessionUiAction.SetupTopic -> viewModel.onTopicSelected(it.topic)
                        is TarotSessionUiAction.ReplyQuestion -> viewModel.onQuestionReply(it.chat)
                        TarotSessionUiAction.EndSession -> viewModel.onEndSession()
                        TarotSessionUiAction.OnCardDraw -> viewModel.onCardDraw()
                        TarotSessionUiAction.BeGoodBoyClick -> viewModel.onBeGoodBoyClick()
                    }
                }
            )
        }
    }
}
