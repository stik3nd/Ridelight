package com.rdireito.ridelight.data.model

import com.google.gson.annotations.SerializedName

data class Estimate(
    val vehicleType: Vehicle,
    val totalPrice: String?,
    val priceFormatted: String?,
    val currency: String?,
    val currencySymbol: String?,
    val eta: ETA?
)

data class Vehicle(
    @SerializedName("_id") val id: String,
    val name: String,
    val shortName: String,
    val description: String,
    val icons: Icon,
    val icon: String,
    val serviceType: String?
)

data class Icon(
    val regular: String
)

data class ETA(
    val min: String,
    val max: String,
    val formatted: String
)