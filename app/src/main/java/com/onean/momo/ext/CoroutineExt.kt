package com.onean.momo.ext


import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

val defaultExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Timber.e(throwable)
}

fun CoroutineScope.safeLaunch(
    context: CoroutineContext = defaultExceptionHandler,
    block: suspend CoroutineScope.() -> Unit
) = launch(context = context, block = block)

suspend fun <T> retry(
    times: Int = Int.MAX_VALUE,
    initialRetryDelay: Long = 500, // 0.5 second
    maxDelay: Long = 10000, // 10 seconds
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialRetryDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: Exception) {
            // e.printStackTrace()
            Timber.w("schedule next attempt: $currentDelay ms, $e")
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block() // last attempt
}

/**
 * Wait until the given condition is satisfied
 *
 * @param timeoutMs time in milliseconds
 * @param retryDelayMs wait for a retryDelayMs time and check the condition again
 * @return true if the condition is satisfied before reaching the timeout time, otherwise false
 */
suspend inline fun waitUntil(
    timeoutMs: Long = 15_000L,
    retryDelayMs: Long = 500,
    crossinline predicate: () -> Boolean,
): Boolean {
    return withTimeoutOrNull(timeoutMs) {
        while (!predicate()) {
            delay(retryDelayMs)
        }
        true
    } ?: false
}

suspend fun <T> awaitAtLeast(
    delayMs: Long = 0, // 0 milliseconds
    block: suspend () -> T
): T {
    return if (delayMs == 0L) {
        block()
    } else {
        coroutineScope {
            val durationAsync = async { delay(delayMs) }
            val result = async { block() }
            durationAsync.await()
            result.await()
        }
    }
}

fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { upstream ->
        val currentTime = System.currentTimeMillis()
        val mayEmit = currentTime - lastEmissionTime > windowDuration
        if (mayEmit) {
            lastEmissionTime = currentTime
            emit(upstream)
        }
    }
}

suspend inline fun repeatAction(
    initialDelayMs: Long = 0,
    intervalMs: Long = 0,
    times: Int = Int.MAX_VALUE,
    crossinline action: suspend (times: Int) -> Unit
) {
    delay(initialDelayMs)
    repeat(times) {
        action(it)
        delay(intervalMs)
    }
}

suspend inline fun <T : Any, R : Any> Iterable<T>.mapConcurrently(
    crossinline transform: suspend (T) -> R?
): List<R?> = coroutineScope {
    map { item ->
        async {
            runCatching {
                transform(item)
            }.getOrElse {
                Timber.w(it)
                null
            }
        }
    }.awaitAll()
}

suspend inline fun <T : Any, R : Any> Iterable<T>.mapConcurrentlyIndexed(
    crossinline transform: suspend (index: Int, T) -> R?
): List<R?> = coroutineScope {
    mapIndexed { index, item ->
        async {
            runCatching {
                transform(index, item)
            }.getOrElse {
                Timber.w(it)
                null
            }
        }
    }.awaitAll()
}

