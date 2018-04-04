package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.data.repository.AddressRepository
import com.rdireito.ridelight.data.repository.AddressRepositoryImpl
import com.rdireito.ridelight.data.repository.EstimateRepository
import com.rdireito.ridelight.data.repository.EstimateRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun providesEstimateRepository(
        estimateRepositoryImpl: EstimateRepositoryImpl
    ): EstimateRepository


    @Binds
    @Singleton
    abstract fun providesAddressRepository(
        addressRepositoryImpl: AddressRepositoryImpl
    ): AddressRepository

}
