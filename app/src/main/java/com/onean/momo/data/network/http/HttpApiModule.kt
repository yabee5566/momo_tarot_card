package com.onean.momo.data.network.http

import com.onean.momo.data.network.repo.ai.tarot_card_backend.TarotCardApiService
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class HttpApiModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            // ip for emulator to connect to localhost
            // .baseUrl("https://tarot-card-backend-736480400874.asia-east1.run.app")
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Reusable
    fun provideTarotCardApiService(
        retrofit: Retrofit
    ): TarotCardApiService {
        return retrofit.create(TarotCardApiService::class.java)
    }
}
