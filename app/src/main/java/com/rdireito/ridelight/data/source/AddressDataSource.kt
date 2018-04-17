package com.rdireito.ridelight.data.source

import com.rdireito.ridelight.data.model.Address
import io.reactivex.Maybe

interface AddressDataSource {

    fun addresses(query: String): Maybe<List<Address>>

    fun addresses(lat: Double, lng: Double): Maybe<List<Address>>

    fun addressByLocation(lat: Double, lng: Double): Maybe<Address>

}
