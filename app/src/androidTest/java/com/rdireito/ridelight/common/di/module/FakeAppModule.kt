package com.rdireito.ridelight.common.di.module

import dagger.Module
import android.app.Application
import android.content.Context
import com.rdireito.ridelight.TestApp
import dagger.Binds

@Module(includes = [
    AndroidModule::class,
    LogModule::class
])
abstract class FakeAppModule {

    @Binds
    abstract fun application(app: TestApp): Application

    @Binds
    abstract fun applicationContext(app: TestApp): Context

}
