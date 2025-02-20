package com.onean.momo.data.network.interceptor

import com.onean.momo.data.network.exception.ServerResponseError
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class ApiResponseInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful) {
            return response
        }

        throw ServerResponseError(response.code, response.message)
    }
}
