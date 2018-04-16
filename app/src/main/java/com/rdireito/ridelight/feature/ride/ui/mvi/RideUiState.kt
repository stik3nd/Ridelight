package com.rdireito.ridelight.feature.ride.ui.mvi

import com.rdireito.ridelight.common.architecture.BaseUiState
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.model.Estimate
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class RideUiState(
    val isLoading: Boolean = false,
    val estimates: @RawValue List<Estimate> = emptyList(),
    val error: Throwable? = null,
    val dropoffAdress: Address? = null,
    val pickupAdress: Address? = null,
    val showPickupFields: Boolean = false,
    val showProducts: Boolean = false,
    val invalidAddress: Boolean = false
) : BaseUiState {

    companion object {
        fun idle(): RideUiState = RideUiState()
    }

}

