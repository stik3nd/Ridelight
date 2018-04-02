package com.rdireito.ridelight.data.model

data class Address(
    val name: String,
    val address: String,
    val location: Location
) {

    companion object {
        val ABSENT: Address = Address(
            "", "", Location(0.0, 0.0)
        )
    }

}
