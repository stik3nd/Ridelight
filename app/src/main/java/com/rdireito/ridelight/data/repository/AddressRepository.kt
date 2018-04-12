package com.rdireito.ridelight.data.repository

import com.rdireito.ridelight.data.model.Address
import io.reactivex.Maybe
import io.reactivex.Single

interface AddressRepository {

    fun addresses(query: String): Maybe<List<Address>>
    fun addressByLocation(lat: Double, lng: Double): Maybe<Address>

}
