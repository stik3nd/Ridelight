package com.rdireito.ridelight

import com.rdireito.ridelight.data.model.*
import java.util.*

object Fabricator {

    fun address() = Address(
        name = "I'm a cool address name",
        address = "Like really I'm a really cool address",
        city = "Cool addressesville",
        country = "Countryeah",
        location = location()
    )

    fun otherAddress() = Address(
        name = "I'm probably a less cool address name",
        address = "Like really I can't be as cool as the cool address",
        city = "Addressesville",
        country = "Countryeah",
        location = location()
    )

    fun location() = Location(0.0, 0.0)

    fun estimate1() = Estimate(
        vehicleType = Vehicle(
            Random().nextInt().toString(),
            name = "Cabify Lite Corp",
            description = "Our flagship quality, made to fit your business",
            icons = Icon("https://test.cabify.com/images/icons/vehicle_type/lite_54.png")
        ),
        priceFormatted = "R\$17.41",
        eta = null
    )

    fun estimate2() = Estimate(
        vehicleType = Vehicle(
            Random().nextInt().toString(),
            name = "Black taxi",
            description = "Agility and Luxury with the same price as a normal taxi",
            icons = Icon("https://test.cabify.com/images/icons/vehicle_type/taxi_54.png")
        ),
        priceFormatted = "R\$23.41",
        eta = null
    )

}
