package com.rdireito.ridelight.feature.main.ui.activity

import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.common.architecture.BaseUiIntent

sealed class MainUiIntent : BaseUiIntent {

    object InitialIntent : MainUiIntent()
    data class ConfirmDropoffLoadIntent(val dropoff: Address) : MainUiIntent()
//    data class LoadEstimatesIntent() ; MainIntent()
//    data class ChangeDropoffIntent(val address: Address) : MainIntent()

}