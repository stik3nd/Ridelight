package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.feature.main.ui.activity.MainActivity
import com.rdireito.ridelight.common.di.scope.ActivityScope
import com.rdireito.ridelight.feature.main.MainActivityModule
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivity
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelBinderModule::class])
abstract class AndroidBinderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindsMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [AddressSearchActivityModule::class])
    abstract fun bindsSearchAddressActivity(): AddressSearchActivity

}
