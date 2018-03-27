package com.rdireito.ridelight.common.di.module

import android.arch.lifecycle.ViewModelProvider
import com.rdireito.ridelight.common.di.AppViewModelFactory
import dagger.Binds
import dagger.Module

@Module
internal abstract class ViewModelBinderModule {

    @Binds
    internal abstract fun bindsViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory

}
