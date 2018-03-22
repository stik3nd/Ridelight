package com.rdireito.ridelight.data.model.request

import com.google.gson.annotations.SerializedName

data class EstimateRequest(
        val stops: List<Stop>,
        val startAt: String? = null
)

data class Stop(
        @SerializedName("loc") val location: List<Double>,
        val name: String,
        @SerializedName("addr") val address: String,
        @SerializedName("num") val number: String,
        val city: String,
        val country: String
)