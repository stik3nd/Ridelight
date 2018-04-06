package com.rdireito.ridelight.feature.ride.ui.mvi

import arrow.core.Option
import arrow.syntax.option.none
import com.rdireito.ridelight.common.architecture.BaseUiState
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.model.Estimate

data class RideUiState(
    val isLoading: Boolean = false,
    val estimates: List<Estimate> = emptyList(),
    val error: Throwable? = null,
    val invokeChangeDropoff: Boolean = false,
    val invokeChangePickup: Boolean = false,
    val dropoffAdress: Option<Address> = none(),
    val pickupAdress: Option<Address> = none()
) : BaseUiState {

    companion object {
        fun idle(): RideUiState = RideUiState()
    }

}

