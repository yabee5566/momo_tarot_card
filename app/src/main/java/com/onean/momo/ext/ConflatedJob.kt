package com.onean.momo.ext

import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.Job

// ref: https://gpeal.medium.com/ten-androidlifehacks-you-can-use-today-9f74186fc207
//
// Tip 8: ConflatedJob
// A common action with coroutines is to cancel a previous instance of a job before launching a new one
// such as if a user pulls to refresh. ConflatedJob automatically cancels the previous job when launching a new one.
// NOTE: this doesn't currently join the existing job,
// it cancels the old one and launches the new one right away.
// We are exploring some options for situations in which you need to wait for cancellation prior to launching the new job.
class ConflatedJob {

    private var job: Job? = null
    private var prevJob: Job? = null

    val isActive get() = job?.isActive ?: false

    @Synchronized
    operator fun plusAssign(newJob: Job) {
        cancel()
        job = newJob
    }

    fun cancel() {
        job?.cancel()
        prevJob = job
    }

    fun start() {
        job?.start()
    }

    /**
     * This can be used inside newly started job to await completion of previous job.
     */
    suspend fun joinPreviousJob() {
        val thisJob = coroutineContext[Job]
        val jobToJoin = synchronized(this) { if (job == thisJob) prevJob else job }
        jobToJoin?.join()
    }
}
