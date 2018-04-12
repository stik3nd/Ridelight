package com.rdireito.ridelight.feature.ride.ui.mvi

import android.location.Location
import arrow.core.Option
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.common.architecture.BaseUiIntent
import com.rdireito.ridelight.common.ui.ActivityResult

sealed class RideUiIntent : BaseUiIntent {

    object InitialIntent : RideUiIntent()
    object ChangeDropoffIntent : RideUiIntent()
    object ChangePickupIntent : RideUiIntent()
    data class OnActivityResultIntent(val activityResult: ActivityResult) : RideUiIntent()
    data class ConfirmDropoffIntent(val address: Option<Address>) : RideUiIntent()
    data class ConfirmPickupIntent(val pickup: Option<Address>, val dropoff: Option<Address>) : RideUiIntent()
    data class NewLocationIntent(val location: Option<Location>) : RideUiIntent()

}