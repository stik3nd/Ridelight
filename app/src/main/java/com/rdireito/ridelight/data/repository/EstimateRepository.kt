package com.rdireito.ridelight.data.repository

import com.rdireito.ridelight.data.model.Estimate
import com.rdireito.ridelight.data.model.request.EstimateRequest
import io.reactivex.Single

interface EstimateRepository {

    fun estimates(estimateRequest: EstimateRequest): Single<List<Estimate>>

}
