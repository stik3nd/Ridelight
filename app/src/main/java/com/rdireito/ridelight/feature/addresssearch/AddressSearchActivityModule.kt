package com.rdireito.ridelight.feature.addresssearch.ui

import android.arch.lifecycle.ViewModel
import com.rdireito.ridelight.common.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AddressSearchActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddressSearchViewModel::class)
    abstract fun bindsMainViewModel(viewModel: AddressSearchViewModel): ViewModel

}
