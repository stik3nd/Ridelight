package com.rdireito.ridelight.common.di.module

import dagger.Module
import android.app.Application
import android.content.Context
import com.rdireito.ridelight.App
import dagger.Binds
import javax.inject.Singleton
import dagger.Provides


@Module
abstract class AppModule {

    @Binds
    abstract fun application(app: App): Application

}
