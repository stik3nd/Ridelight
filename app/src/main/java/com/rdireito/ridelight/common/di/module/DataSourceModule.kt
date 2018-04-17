package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.data.source.AddressDataSource
import com.rdireito.ridelight.data.source.AddressDataSourceImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun providesAddressDataSource(
        addressDataSourceImpl: AddressDataSourceImpl
    ): AddressDataSource

}
