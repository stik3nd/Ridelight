package com.rdireito.ridelight.feature.ride.ui.mvi

import android.location.Location
import arrow.core.Option
import com.rdireito.ridelight.common.architecture.BaseAction
import com.rdireito.ridelight.common.ui.ActivityResult
import com.rdireito.ridelight.data.model.Address

sealed class RideAction : BaseAction {

    object InitialAction : RideAction()
    object SkipAction : RideAction()
    object InvokeChangeDropoffAction : RideAction()
    object InvokeChangePickupAction : RideAction()
    data class CheckActivityResultAction(val activityResult: ActivityResult) : RideAction()
    data class ConfirmDropoffAction(val dropoffAddress: Option<Address>, val currentPosition: Option<Location>) : RideAction()
    data class FetchEstimatesAction(val pickup: Option<Address>, val dropoff: Option<Address>) : RideAction()

}
