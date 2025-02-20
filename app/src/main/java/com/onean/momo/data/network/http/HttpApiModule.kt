package com.onean.momo.data.network.http

import com.onean.momo.data.network.ApiConst
import com.onean.momo.data.network.interceptor.di.LogInterceptor
import com.onean.momo.data.network.interceptor.di.ResponseInterceptor
import com.onean.momo.data.network.repo.ai.tarot_card_backend.TarotCardApiService
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class HttpApiModule {
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
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

    @Provides
    @Singleton
    fun provideOkhttpClient(
        @LogInterceptor logInterceptor: Interceptor,
        @ResponseInterceptor responseInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .readTimeout(ApiConst.API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(ApiConst.API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectionPool(
                ConnectionPool(
                    maxIdleConnections = ApiConst.CONNECTION_POOL_SIZE,
                    keepAliveDuration = ApiConst.KEEP_ALIVE_TIMEOUT,
                    timeUnit = TimeUnit.SECONDS
                )
            )
            .addInterceptor(logInterceptor)
            .addInterceptor(responseInterceptor)
            .build()
    }
}
