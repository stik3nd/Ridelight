package com.rdireito.ridelight.data.model

data class Address(
    val name: String,
    val address: String,
    val number: String = "s/n",
    val city: String,
    val country: String,
    val location: Location
) {

    constructor(androidAddress: android.location.Address) : this(
        name = androidAddress.thoroughfare ?: androidAddress.getAddressLine(0) ?: "",
        address = androidAddress.thoroughfare ?: androidAddress.getAddressLine(0) ?: "",
        city = androidAddress.locality ?: androidAddress.subAdminArea.orEmpty(),
        country = androidAddress.countryName.orEmpty(),
        location = Location(androidAddress.latitude, androidAddress.longitude)
    )

    companion object {
        val ABSENT: Address = Address(
            "", "", "", "", "", Location(0.0, 0.0)
        )
    }

}
