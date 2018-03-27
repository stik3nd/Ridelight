package com.rdireito.ridelight.feature.main.ui.activity

import android.location.Address
import com.rdireito.ridelight.common.architecture.BaseAction

sealed class MainAction : BaseAction {

    // delete this later when you know what to do on initial intent
    object InitialAction : MainAction()
    data class FetchEstimatesAction(val dropoff: Address) : MainAction()

}
