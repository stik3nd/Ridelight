package com.rdireito.ridelight.common.di.module

import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

@Module
class LogModule {

    @Provides
    @Singleton
    fun providesTimber(): Timber.Tree = Timber.DebugTree()

}
