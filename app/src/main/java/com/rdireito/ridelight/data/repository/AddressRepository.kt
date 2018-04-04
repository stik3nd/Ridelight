package com.rdireito.ridelight.data.repository

import com.rdireito.ridelight.data.model.Address
import io.reactivex.Single

interface AddressRepository {

    fun addresses(query: String): Single<List<Address>>

}
