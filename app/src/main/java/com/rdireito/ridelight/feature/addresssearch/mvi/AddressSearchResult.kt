package com.rdireito.ridelight.feature.addresssearch.mvi

import com.rdireito.ridelight.common.architecture.BaseResult
import com.rdireito.ridelight.data.model.Address

sealed class AddressSearchResult : BaseResult {

    sealed class ClearAddressResult : AddressSearchResult() {
        object Success : ClearAddressResult()
    }

    sealed class AllowClearAddressResult : AddressSearchResult() {
        object Allow : AllowClearAddressResult()
        object Deny : AllowClearAddressResult()
    }

    sealed class FetchAddressesResult : AddressSearchResult() {
        object Loading : FetchAddressesResult()
        data class Error(val error: Throwable) : FetchAddressesResult()
        data class Success(val addresses: List<Address>) : FetchAddressesResult()
    }

    sealed class SelectAddressResult : AddressSearchResult() {
        data class Success(val address: Address) : SelectAddressResult()
    }

}
