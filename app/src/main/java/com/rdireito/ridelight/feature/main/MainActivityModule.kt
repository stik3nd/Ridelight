package com.rdireito.ridelight.feature.main

import android.arch.lifecycle.ViewModel
import com.rdireito.ridelight.common.di.ViewModelKey
import com.rdireito.ridelight.feature.main.ui.activity.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindsMainViewModel(viewModel: MainViewModel): ViewModel

}
