package com.rdireito.ridelight

import com.rdireito.ridelight.common.di.component.DaggerTestAppComponent
import com.rdireito.ridelight.common.di.module.FakeApiModule
import com.rdireito.ridelight.common.di.module.FakeDataSourceModule
import com.rdireito.ridelight.common.di.module.FakeRepositoryModule
import dagger.android.AndroidInjector
import javax.inject.Inject

class TestApp : App() {

    @Inject
    lateinit var dataSourceHolder: FakeDataSourceModule.Holder

    @Inject
    lateinit var repositoryHolder: FakeRepositoryModule.Holder

    @Inject
    lateinit var apiHolder: FakeApiModule.Holder

    override fun applicationInjector(): AndroidInjector<out App> {
        return DaggerTestAppComponent.builder().create(this)
    }

}
