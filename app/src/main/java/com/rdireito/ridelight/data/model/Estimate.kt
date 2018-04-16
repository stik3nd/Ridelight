package com.rdireito.ridelight.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Estimate(
    val vehicleType: Vehicle,
    val priceFormatted: String?,
    val eta: Eta?
) : Parcelable

@Parcelize
data class Vehicle(
    @SerializedName("_id") val id: String,
    val name: String,
    val description: String,
    val icons: Icon
) : Parcelable

@Parcelize
data class Icon(
    val regular: String
) : Parcelable

@Parcelize
data class Eta(
    val min: String,
    val max: String,
    val formatted: String
) : Parcelable