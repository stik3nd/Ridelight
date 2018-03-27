package com.rdireito.ridelight.feature.main.ui.activity

import com.rdireito.ridelight.common.architecture.BaseResult
import com.rdireito.ridelight.data.model.Estimate

sealed class MainResult : BaseResult {

    sealed class InitialResult : MainResult() {
        object Initial : InitialResult()
    }

    sealed class FetchEstimatesResult : MainResult() {
        object Loading : FetchEstimatesResult()
        data class Error(val error: Throwable) : FetchEstimatesResult()
        data class Success(val estimates: List<Estimate>) : FetchEstimatesResult()
    }

}
