package com.rdireito.ridelight.executor

import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import javax.inject.Inject

class AndroidTestSchedulerComposer @Inject constructor() : SchedulerComposer {

    companion object {
        val computationScheduler = TestScheduler()
    }

    override fun io(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()

    override fun computation(): Scheduler = computationScheduler

}
