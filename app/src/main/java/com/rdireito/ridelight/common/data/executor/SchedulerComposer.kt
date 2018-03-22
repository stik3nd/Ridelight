package com.rdireito.ridelight.common.data.executor

import io.reactivex.Scheduler

interface SchedulerComposer {

    fun io(): Scheduler
    fun ui(): Scheduler
    fun computation(): Scheduler

}
