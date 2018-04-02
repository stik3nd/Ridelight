package com.rdireito.ridelight.feature.addresssearch.mvi

import com.rdireito.ridelight.common.architecture.BaseUiState
import com.rdireito.ridelight.data.model.Address

data class AddressSearchUiState(
    val isLoading: Boolean,
    val addresses: List<Address>,
    val error: Throwable?,
    val selectedAddress: Address
) : BaseUiState {

    companion object {
        fun idle(): AddressSearchUiState = AddressSearchUiState(
            isLoading = false,
            addresses = emptyList(),
            error = null,
            selectedAddress = Address.ABSENT
        )

        fun addressSelected(selectedAddress: Address): AddressSearchUiState = AddressSearchUiState(
            isLoading = false,
            addresses = emptyList(),
            error = null,
            selectedAddress = selectedAddress
        )
    }


}

fun AddressSearchUiState.loading(): AddressSearchUiState = this.copy(
    isLoading = true,
    error = null
)

fun AddressSearchUiState.error(throwable: Throwable): AddressSearchUiState = this.copy(
    isLoading = false,
    addresses = emptyList(),
    error = throwable
)

fun AddressSearchUiState.content(addresses: List<Address>): AddressSearchUiState = this.copy(
    isLoading = false,
    addresses = addresses,
    error = null
)
