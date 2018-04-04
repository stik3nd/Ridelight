package com.rdireito.ridelight.data.repository

import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.source.AddressDataSource
import io.reactivex.Single
import javax.inject.Inject


class AddressRepositoryImpl @Inject constructor(
    private val addressDataSource: AddressDataSource
) : AddressRepository {

    override fun addresses(query: String): Single<List<Address>> =
        addressDataSource.addresses(query)

}
