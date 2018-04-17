package com.rdireito.ridelight.common.di.component

import com.rdireito.ridelight.TestApp
import com.rdireito.ridelight.common.di.module.AndroidBinderModule
import com.rdireito.ridelight.common.di.module.FakeAppModule
import com.rdireito.ridelight.common.di.module.FakeDataModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AndroidBinderModule::class,
    FakeAppModule::class,
    FakeDataModule::class
])

interface TestAppComponent : AndroidInjector<TestApp> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<TestApp>()

}
