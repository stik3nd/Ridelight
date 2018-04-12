package com.rdireito.ridelight.data.source

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.*

@SuppressLint("MissingPermission")
class BoundLocationManager(
    lifecycleOwner: LifecycleOwner,
    private val context: Activity,
    private val locationCallback: LocationCallback
) : LifecycleObserver {

    private val locationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(context) }
    private val locationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(1000)

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun addLocationUpdates() {
        locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        locationClient.lastLocation
            .addOnSuccessListener(context, { location ->
                if (location != null) {
                    locationCallback.onLocationResult(LocationResult.create(listOf(location)))
                }
            })
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun removeLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback)
    }

}
