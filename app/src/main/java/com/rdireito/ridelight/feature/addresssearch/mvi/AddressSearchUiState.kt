package com.rdireito.ridelight.feature.addresssearch.mvi

import arrow.core.Option
import arrow.syntax.option.none
import arrow.syntax.option.some
import com.rdireito.ridelight.common.architecture.BaseUiState
import com.rdireito.ridelight.data.model.Address

data class AddressSearchUiState(
    val isLoading: Boolean = false,
    val clearQuery: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val error: Throwable? = null,
    val selectedAddress: Option<Address> = none(),
    val hasClearButton: Boolean = false
) : BaseUiState {

    companion object {
        fun idle(): AddressSearchUiState = AddressSearchUiState()

        fun addressSelected(selectedAddress: Address): AddressSearchUiState = AddressSearchUiState(
            clearQuery = true,
            selectedAddress = selectedAddress.some()
        )
    }

}

fun AddressSearchUiState.loading(): AddressSearchUiState = this.copy(
    isLoading = true,
    error = null,
    clearQuery = false
)

fun AddressSearchUiState.error(throwable: Throwable): AddressSearchUiState = this.copy(
    isLoading = false,
    addresses = emptyList(),
    error = throwable,
    clearQuery = false
)

fun AddressSearchUiState.content(addresses: List<Address>): AddressSearchUiState = this.copy(
    isLoading = false,
    addresses = addresses,
    error = null,
    clearQuery = false
)
