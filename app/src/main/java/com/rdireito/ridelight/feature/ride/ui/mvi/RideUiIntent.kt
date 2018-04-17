package com.rdireito.ridelight.feature.ride.ui.mvi

import arrow.core.Either
import arrow.core.Option
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.common.architecture.BaseUiIntent
import com.rdireito.ridelight.common.ui.ActivityResult.FailureWithData
import com.rdireito.ridelight.common.ui.ActivityResult.SuccessWithData
import com.rdireito.ridelight.data.model.Location

sealed class RideUiIntent : BaseUiIntent {

    data class InitialIntent(val restoredUiState: RideUiState?) : RideUiIntent()
    object ChangeDropoffIntent : RideUiIntent()
    object ChangePickupIntent : RideUiIntent()
    data class OnActivityResultIntent(val eitherActivityResult: Either<FailureWithData, SuccessWithData>) : RideUiIntent()
    data class ConfirmDropoffIntent(val address: Option<Address>) : RideUiIntent()
    data class ConfirmPickupIntent(val pickup: Option<Address>, val dropoff: Option<Address>) : RideUiIntent()
    data class ProductTryAgainIntent(val pickup: Option<Address>, val dropoff: Option<Address>) : RideUiIntent()
    data class NewLocationIntent(val location: Option<Location>) : RideUiIntent()

}