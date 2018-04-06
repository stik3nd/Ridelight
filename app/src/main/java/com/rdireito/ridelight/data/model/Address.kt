package com.rdireito.ridelight.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val name: String,
    val address: String,
    val number: String = "s/n",
    val city: String,
    val country: String,
    val location: Location
) : Parcelable {

    constructor(androidAddress: android.location.Address) : this(
        name = androidAddress.thoroughfare ?: androidAddress.getAddressLine(0) ?: "",
        address = androidAddress.getAddressLine(0) ?: androidAddress.thoroughfare ?: "",
        city = androidAddress.locality ?: androidAddress.subAdminArea.orEmpty(),
        country = androidAddress.countryName.orEmpty(),
        location = Location(androidAddress.latitude, androidAddress.longitude)
    )

}
