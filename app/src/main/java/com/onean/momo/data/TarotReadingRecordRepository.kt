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

package com.onean.momo.data

import com.onean.momo.data.local.database.TarotReadingRecord
import com.onean.momo.data.local.database.TarotReadingRecordDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TarotReadingRecordRepository {
    val tarotReadingRecords: Flow<List<String>>

    suspend fun add(name: String)
}

class DefaultTarotReadingRecordRepo @Inject constructor(
    private val tarotReadingRecordDao: TarotReadingRecordDao
) : TarotReadingRecordRepository {

    override val tarotReadingRecords: Flow<List<String>> =
        tarotReadingRecordDao.getTarotReadingRecords().map { items -> items.map { it.name } }

    override suspend fun add(name: String) {
        tarotReadingRecordDao.insertTarotReadingRecord(TarotReadingRecord(name = name))
    }
}
