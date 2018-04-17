package com.rdireito.ridelight.executor

import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler

class TestSchedulerComposer : SchedulerComposer {

    val computationScheduler = TestScheduler()

    override fun io(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = Schedulers.trampoline()

    override fun computation(): Scheduler = computationScheduler

}
