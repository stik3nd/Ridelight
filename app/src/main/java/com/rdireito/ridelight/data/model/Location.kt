package com.rdireito.ridelight.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f
) : Parcelable {

    constructor(androidLocation: android.location.Location) : this(
        androidLocation.latitude, androidLocation.longitude, androidLocation.accuracy)

}
