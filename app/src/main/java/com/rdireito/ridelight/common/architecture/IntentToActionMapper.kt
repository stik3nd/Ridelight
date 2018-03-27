package com.rdireito.ridelight.common.architecture

interface UiIntentToActionMapper<in I : BaseUiIntent, out A : BaseAction> {
    fun intentToAction(intent: I): A
}
