package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.common.data.executor.AppSchedulerComposer
import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module(includes = [
    DataSourceModule::class,
    RepositoryModule::class,
    NetworkModule::class
])
abstract class DataModule {

    @Binds
    @Reusable
    abstract fun bindsSchedulerComposer(schedulerManager: AppSchedulerComposer): SchedulerComposer

}
