package com.rdireito.ridelight.feature.ride

import android.arch.lifecycle.ViewModel
import com.rdireito.ridelight.common.di.ViewModelKey
import com.rdireito.ridelight.common.di.scope.ActivityScope
import com.rdireito.ridelight.feature.ride.ui.RideActivity
import com.rdireito.ridelight.feature.ride.ui.RideViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class RideActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(RideViewModel::class)
    abstract fun bindsMainViewModel(viewModel: RideViewModel): ViewModel

}
