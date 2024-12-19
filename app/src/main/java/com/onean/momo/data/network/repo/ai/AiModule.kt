package com.onean.momo.data.network.repo.ai

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    abstract fun bindTarotAiRepo(impl: OffLineDummyTarotAiRepoImpl): TarotAiRepo
}
