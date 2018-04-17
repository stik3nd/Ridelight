package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.data.repository.AddressRepository
import com.rdireito.ridelight.data.repository.AddressRepositoryImpl
import com.rdireito.ridelight.data.repository.EstimateRepository
import com.rdireito.ridelight.data.repository.EstimateRepositoryImpl
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import javax.inject.Singleton

@Module
class FakeRepositoryModule {

    @Provides
    @Singleton
    fun providesEstimateRepository(
        estimateRepositoryImpl: EstimateRepositoryImpl
    ): EstimateRepository =
        Mockito.spy(estimateRepositoryImpl)


    @Provides
    @Singleton
    fun providesAddressRepository(
        addressRepositoryImpl: AddressRepositoryImpl
    ): AddressRepository =
        Mockito.spy(addressRepositoryImpl)

    @Provides
    fun provideMocks(
        estimateRepository: EstimateRepository,
        addressRepository: AddressRepository
    ) = Holder(
        estimateRepository,
        addressRepository
    )

    class Holder(
        val estimateRepository: EstimateRepository,
        val addressRepository: AddressRepository
    )

}
