package com.rdireito.ridelight.feature.ride.ui.mvi

import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.common.architecture.BaseUiIntent
import com.rdireito.ridelight.common.ui.ActivityResult

sealed class RideUiIntent : BaseUiIntent {

    object InitialIntent : RideUiIntent()
    object ChangeDropoffIntent : RideUiIntent()
    object ChangePickupIntent : RideUiIntent()
    data class OnActivityResultIntent(val activityResult: ActivityResult) : RideUiIntent()
    data class ConfirmDropoffIntent(val address: Address) : RideUiIntent()
    data class ConfirmPickupIntent(val address: Address) : RideUiIntent()

}