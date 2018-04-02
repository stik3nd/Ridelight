package com.rdireito.ridelight.feature.main.ui.activity

import com.rdireito.ridelight.common.architecture.BaseAction
import com.rdireito.ridelight.data.model.Address

sealed class MainAction : BaseAction {

    // delete this later when you know what to do on initial intent
    object InitialAction : MainAction()
    data class FetchEstimatesAction(val dropoff: Address) : MainAction()

}
