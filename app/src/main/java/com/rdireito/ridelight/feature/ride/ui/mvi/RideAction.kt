package com.rdireito.ridelight.feature.ride.ui.mvi

import android.location.Location
import android.os.Bundle
import arrow.core.Either
import arrow.core.Option
import com.rdireito.ridelight.common.architecture.BaseAction
import com.rdireito.ridelight.common.ui.ActivityResult.FailureWithData
import com.rdireito.ridelight.common.ui.ActivityResult.SuccessWithData
import com.rdireito.ridelight.data.model.Address

sealed class RideAction : BaseAction {

    data class InitialAction(val savedInstanceState: Bundle?) : RideAction()
    object SkipAction : RideAction()
    data class CheckActivityResultAction(val eitherActivityResult: Either<FailureWithData, SuccessWithData>) : RideAction()
    data class ConfirmDropoffAction(val dropoffAddress: Option<Address>, val currentPosition: Option<Location>) : RideAction()
    data class FetchEstimatesAction(val pickup: Option<Address>, val dropoff: Option<Address>) : RideAction()

}
