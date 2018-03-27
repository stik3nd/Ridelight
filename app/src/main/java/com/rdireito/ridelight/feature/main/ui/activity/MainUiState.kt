package com.rdireito.ridelight.feature.main.ui.activity

import com.rdireito.ridelight.common.architecture.BaseUiState
import com.rdireito.ridelight.data.model.Estimate

data class MainUiState(
    val isLoading: Boolean,
    val estimates: List<Estimate>,
    val error: Throwable?
) : BaseUiState {

    companion object {
        fun idle(): MainUiState = MainUiState(
            isLoading = false,
            estimates = emptyList(),
            error = null
        )
    }

}

