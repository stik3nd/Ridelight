package com.rdireito.ridelight.common.ui

sealed class ActivityResult(open val requestCode: Int) {

    data class SuccessWithData(override val requestCode: Int, val data: Map<String, Any>) : ActivityResult(requestCode)
    data class FailureWithData(override val requestCode: Int, val data: Map<String, Any>) : ActivityResult(requestCode)

}
