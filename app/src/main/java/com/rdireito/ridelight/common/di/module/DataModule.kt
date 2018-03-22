package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.common.data.executor.AppSchedulerComposer
import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [
    RepositoryModule::class,
    NetworkModule::class
])
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindsSchedulerComposer(schedulerManager: AppSchedulerComposer): SchedulerComposer

}
