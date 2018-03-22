package com.rdireito.ridelight.data.network.api

import com.rdireito.ridelight.data.model.Estimate
import com.rdireito.ridelight.data.model.request.EstimateRequest
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface EstimateApi {

    @POST("/api/v2/estimate")
    fun estimates(@Body body: EstimateRequest): Single<List<Estimate>>

}
