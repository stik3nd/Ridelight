package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.data.network.api.EstimateApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun providesEstimateApi(retrofit: Retrofit): EstimateApi {
        return retrofit.create(EstimateApi::class.java)
    }

}
