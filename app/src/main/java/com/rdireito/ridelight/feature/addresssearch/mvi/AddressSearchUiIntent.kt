package com.rdireito.ridelight.feature.addresssearch.mvi

import com.rdireito.ridelight.common.architecture.BaseUiIntent
import com.rdireito.ridelight.data.model.Address

sealed class AddressSearchUiIntent : BaseUiIntent {

    object ClearAddressIntent : AddressSearchUiIntent()
    data class InputSearchTextIntent(val length: Int) : AddressSearchUiIntent()
    data class ChangeAddressIntent(val query: String) : AddressSearchUiIntent()
    data class SelectAddressIntent(val address: Address) : AddressSearchUiIntent()

}
