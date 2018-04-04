package com.rdireito.ridelight.data.source

import android.location.Geocoder
import com.rdireito.ridelight.data.model.Address
import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class AddressDataSource @Inject constructor(
    private val geocoder: Geocoder
) {

    fun addresses(query: String): Single<List<Address>> = Single.fromCallable {
        geocoder.getFromLocationName(query, 10)
            .map { it.toAddress() }
//            .toList()
    }

    private fun android.location.Address.toAddress(): Address =
        Address(this)

}
