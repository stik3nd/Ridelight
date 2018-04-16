package com.rdireito.ridelight.data.repository

import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.source.AddressDataSource
import io.reactivex.Maybe
import javax.inject.Inject


class AddressRepositoryImpl @Inject constructor(
    private val addressDataSource: AddressDataSource
) : AddressRepository {

    override fun addresses(query: String): Maybe<List<Address>> =
        addressDataSource.addresses(query)

    override fun addressByLocation(lat: Double, lng: Double): Maybe<Address> =
        addressDataSource.addressByLocation(lat, lng)
}
