package com.rdireito.ridelight.common.di.component

import com.rdireito.ridelight.App
import com.rdireito.ridelight.common.di.module.AndroidBinderModule
import com.rdireito.ridelight.common.di.module.AppModule
import com.rdireito.ridelight.common.di.module.DataModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AndroidBinderModule::class,
    AppModule::class,
    DataModule::class
])

interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()

}
