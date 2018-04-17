package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import com.rdireito.ridelight.executor.AndroidTestSchedulerComposer
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module(includes = [
    FakeDataSourceModule::class,
    FakeRepositoryModule::class,
    FakeNetworkModule::class
])
abstract class FakeDataModule {

    @Binds
    @Reusable
    abstract fun bindsSchedulerComposer(schedulerManager: AndroidTestSchedulerComposer): SchedulerComposer

}
