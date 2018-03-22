package com.rdireito.ridelight.common.di.module

import dagger.Module
import android.app.Application
import android.content.Context
import com.rdireito.ridelight.App
import dagger.Binds
import javax.inject.Singleton
import dagger.Provides


@Module(includes = [
    AndroidModule::class,
    LogModule::class
])
abstract class AppModule {

    @Binds
    abstract fun application(app: App): Application

    @Binds
    abstract fun applicationContext(app: App): Context

}
