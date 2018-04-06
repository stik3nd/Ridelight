package com.rdireito.ridelight.feature.ride.ui.mvi

import com.rdireito.ridelight.common.architecture.BaseAction
import com.rdireito.ridelight.common.ui.ActivityResult
import com.rdireito.ridelight.data.model.Address

sealed class RideAction : BaseAction {

    object InitialAction : RideAction()
    object InvokeChangeDropoffAction : RideAction()
    object InvokeChangePickupAction : RideAction()
    data class CheckActivityResultAction(val activityResult: ActivityResult) : RideAction()
    data class FetchEstimatesAction(val dropoff: Address) : RideAction()

}
