package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.feature.ride.ui.RideActivity
import com.rdireito.ridelight.common.di.scope.ActivityScope
import com.rdireito.ridelight.feature.ride.RideActivityModule
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivity
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelBinderModule::class])
abstract class AndroidBinderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [RideActivityModule::class])
    abstract fun bindsMainActivity(): RideActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [AddressSearchActivityModule::class])
    abstract fun bindsSearchAddressActivity(): AddressSearchActivity

}
