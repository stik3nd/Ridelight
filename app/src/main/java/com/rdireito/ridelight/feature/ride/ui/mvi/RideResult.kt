package com.rdireito.ridelight.feature.ride.ui.mvi

import arrow.core.Option
import com.rdireito.ridelight.common.architecture.BaseResult
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.model.Estimate

sealed class RideResult : BaseResult {

    sealed class InitialResult : RideResult() {
        object Initial : InitialResult()
    }

    sealed class InvokeChangeDropoffResult : RideResult() {
        object Invoke : InvokeChangeDropoffResult()
    }

    sealed class InvokeChangePickupResult : RideResult() {
        object Invoke : InvokeChangePickupResult()
    }

    sealed class CheckActivityResult : RideResult() {
        data class Failure(val error: Throwable) : CheckActivityResult()
        data class DropoffSuccess(val address: Address) : CheckActivityResult()
        data class PickupSuccess(val address: Address) : CheckActivityResult()
    }

    sealed class ConfirmDropoffResult : RideResult() {
        object Valid : ConfirmDropoffResult()
        data class ValidWithPickup(val initialPickup: Option<Address>) : ConfirmDropoffResult()
        object Invalid : ConfirmDropoffResult()
        object HideMessage : ConfirmDropoffResult()
    }

    sealed class FetchEstimatesResult : RideResult() {
        object Loading : FetchEstimatesResult()
        data class Error(val error: Throwable) : FetchEstimatesResult()
        data class Success(val estimates: List<Estimate>) : FetchEstimatesResult()
        object InvalidParams : FetchEstimatesResult()
        object HideMessage : FetchEstimatesResult()
    }

}
