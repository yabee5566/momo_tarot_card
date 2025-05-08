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

package com.onean.momo.ui.tarot_reading_record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onean.momo.data.TarotReadingRecordRepository
import com.onean.momo.ui.tarot_reading_record.TarotReadingRecordUiState.Error
import com.onean.momo.ui.tarot_reading_record.TarotReadingRecordUiState.Loading
import com.onean.momo.ui.tarot_reading_record.TarotReadingRecordUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TarotReadingRecordViewModel @Inject constructor(
    private val tarotReadingRecordRepository: TarotReadingRecordRepository
) : ViewModel() {

    val uiState: StateFlow<TarotReadingRecordUiState> = tarotReadingRecordRepository
        .tarotReadingRecords.map<List<String>, TarotReadingRecordUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addTarotReadingRecord(name: String) {
        viewModelScope.launch {
            tarotReadingRecordRepository.add(name)
        }
    }
}

sealed interface TarotReadingRecordUiState {
    data object Loading : TarotReadingRecordUiState
    data class Error(val throwable: Throwable) : TarotReadingRecordUiState
    data class Success(val data: List<String>) : TarotReadingRecordUiState
}
