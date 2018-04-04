package com.rdireito.ridelight.feature.addresssearch.mvi

import com.rdireito.ridelight.common.architecture.BaseAction
import com.rdireito.ridelight.data.model.Address

sealed class AddressSearchAction : BaseAction {

    object ClearAddressAction : AddressSearchAction()
    data class AllowClearAddressAction(val length: Int) : AddressSearchAction()
    data class FetchAddressesAction(val query: String) : AddressSearchAction()
    data class SelectAddressAction(val address: Address) : AddressSearchAction()

}
