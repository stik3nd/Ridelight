package com.rdireito.ridelight.data.source

import android.location.Geocoder
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.model.Location
import dagger.Reusable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject

@Reusable
class AddressDataSource @Inject constructor(
    private val geocoder: Geocoder
) {

    fun addresses(query: String): Maybe<List<Address>> = Maybe.fromCallable {
        geocoder.getFromLocationName(query, MAX_RESULTS)
            .map { it.toAddress() }
    }

    fun addresses(lat: Double, lng: Double): Maybe<List<Address>> = Maybe.fromCallable {
        geocoder.getFromLocation(lat, lng, MAX_RESULTS)
            .map { it.toAddress() }
    }

    fun addressByLocation(lat: Double, lng: Double): Maybe<Address> =
        addresses(lat, lng).map { it.firstOrNull() }

    private fun android.location.Address.toAddress(): Address =
        Address(this)

    companion object {
        const val MAX_RESULTS = 10
    }

}
