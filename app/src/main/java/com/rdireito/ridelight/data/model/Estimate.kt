package com.rdireito.ridelight.data.model

import com.google.gson.annotations.SerializedName

data class Estimate(
    val vehicleType: Vehicle,
    val priceFormatted: String?,
    val eta: ETA?
)

data class Vehicle(
    @SerializedName("_id") val id: String,
    val name: String,
    val description: String,
    val icons: Icon
)

data class Icon(
    val regular: String
)

data class ETA(
    val min: String,
    val max: String,
    val formatted: String
)