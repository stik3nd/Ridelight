package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.data.network.api.EstimateApi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.mockito.Mockito
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class FakeApiModule {

    @Provides
    @Singleton
    fun providesEstimateApi(retrofit: Retrofit): EstimateApi =
        Mockito.mock(EstimateApi::class.java)

    @Provides
    @Singleton
    fun providesMocks(estimateApi: EstimateApi) = Holder(estimateApi)

    class Holder(
        val estimateApi: EstimateApi
    )

}