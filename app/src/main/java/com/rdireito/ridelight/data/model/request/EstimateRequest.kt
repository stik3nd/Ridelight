package com.rdireito.ridelight.data.model.request

import com.google.gson.annotations.SerializedName
import com.rdireito.ridelight.data.model.Address

data class EstimateRequest(
    val stops: List<Stop>,
    val startAt: String? = null
) {
    constructor(pickup: Address, dropoff: Address) : this(
        listOf(pickup.buildStop(), dropoff.buildStop())
    )
}

data class Stop(
    @SerializedName("loc") val location: List<Double>,
    val name: String,
    @SerializedName("addr") val address: String,
    @SerializedName("num") val number: String,
    val city: String,
    val country: String
)

fun Address.buildStop(): Stop = Stop(
    listOf(this.location.latitude, this.location.longitude),
    this.name,
    this.address,
    this.number,
    this.city,
    this.country
)