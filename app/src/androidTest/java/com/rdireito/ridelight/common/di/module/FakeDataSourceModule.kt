package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.data.source.AddressDataSource
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import javax.inject.Singleton

@Module
class FakeDataSourceModule {

    @Provides
    @Singleton
    fun providesAddressDataSource() = Mockito.mock(AddressDataSource::class.java)

    @Provides
    @Singleton
    fun providesMocks(addressDataSource: AddressDataSource) = Holder(addressDataSource)

    class Holder(
        val addressDataSource: AddressDataSource
    )

}
