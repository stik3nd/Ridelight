package com.rdireito.ridelight.data.source

import android.location.Geocoder
import com.rdireito.ridelight.data.model.Address
import io.reactivex.Maybe
import javax.inject.Inject

class AddressDataSourceImpl @Inject constructor(
    private val geocoder: Geocoder
) : AddressDataSource {

    override fun addresses(query: String): Maybe<List<Address>> = Maybe.fromCallable {
        geocoder.getFromLocationName(query, MAX_RESULTS)
            .map { it.toAddress() }
    }

    override fun addresses(lat: Double, lng: Double): Maybe<List<Address>> = Maybe.fromCallable {
        geocoder.getFromLocation(lat, lng, MAX_RESULTS)
            .map { it.toAddress() }
    }

    override fun addressByLocation(lat: Double, lng: Double): Maybe<Address> =
        addresses(lat, lng).map { it.firstOrNull() }

    private fun android.location.Address.toAddress(): Address =
        Address(this)

    companion object {
        const val MAX_RESULTS = 10
    }

}
