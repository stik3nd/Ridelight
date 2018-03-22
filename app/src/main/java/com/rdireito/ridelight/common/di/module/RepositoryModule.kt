package com.rdireito.ridelight.common.di.module

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

}
