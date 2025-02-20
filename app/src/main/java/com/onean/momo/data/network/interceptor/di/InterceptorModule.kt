package com.onean.momo.data.network.interceptor.di

import com.onean.momo.BuildConfig
import com.onean.momo.data.network.interceptor.ApiResponseInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
class InterceptorModule {
    @Provides
    @ResponseInterceptor
    fun provideResponseInterceptor(impl: ApiResponseInterceptor): Interceptor = impl

    @Provides
    @LogInterceptor
    fun provideLogInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
}
