package com.rdireito.ridelight.data.repository

import com.rdireito.ridelight.data.model.Estimate
import com.rdireito.ridelight.data.model.request.EstimateRequest
import com.rdireito.ridelight.data.network.api.EstimateApi
import io.reactivex.Single
import javax.inject.Inject

open class EstimateRepositoryImpl @Inject constructor(
    private val estimateApi: EstimateApi
) : EstimateRepository {

    override fun estimates(estimateRequest: EstimateRequest): Single<List<Estimate>> {
        return estimateApi.estimates(estimateRequest)
    }

}
