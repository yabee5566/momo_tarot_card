package com.onean.momo.data.network

object ApiConst {
    const val API_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 15L
    const val CONN_TIMEOUT_SECONDS = 30L
    const val CONNECTION_POOL_SIZE = 3

    const val MAX_PARALLEL_REQUESTS = 64
    const val MAX_PARALLEL_REQUESTS_PER_HOST = 16
    const val IDLE_CONNECTION_NUMBER = 5
    const val KEEP_ALIVE_TIMEOUT = 50L
}
