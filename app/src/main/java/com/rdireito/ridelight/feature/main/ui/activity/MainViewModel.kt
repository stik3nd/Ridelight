package com.rdireito.ridelight.feature.main.ui.activity

import android.arch.lifecycle.ViewModel
import com.rdireito.ridelight.common.architecture.BaseViewModel
import com.rdireito.ridelight.feature.main.ui.activity.MainAction.*
import com.rdireito.ridelight.feature.main.ui.activity.MainResult.*
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val actionProcessorHolder: MainActionProcessor
) : ViewModel(), BaseViewModel<MainUiIntent, MainUiState> {

    private val intentsSubject = PublishSubject.create<MainUiIntent>()
    private val statesObservable: Observable<MainUiState> = stream()

    override fun processIntents(intents: Observable<MainUiIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<MainUiState> = statesObservable

    private fun stream(): Observable<MainUiState> {
        return intentsSubject
            .map(this::intentToAction)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(MainUiState.idle(), reducer)
            .distinctUntilChanged()
            // Replay the last emitted item on subscription,
            // for when a View rebinds to the ViewModel after rotation.
            .replay(1)
            // Create the stream immediately, no subscribers needed.
            // This allows the stream to stay alive when the UI disconnects and
            // matches the stream lifecycle to the ViewModel's lifecycle.
            .autoConnect(0)
    }

    private fun intentToAction(intent: MainUiIntent): MainAction =
        when (intent) {
            is MainUiIntent.InitialIntent -> InitialAction
            is MainUiIntent.ConfirmDropoffLoadIntent -> FetchEstimatesAction(intent.dropoff)
        }

    companion object {
        private val reducer = BiFunction { previousState: MainUiState, result: MainResult ->
            when (result) {
                is InitialResult -> when (result) {
                    is InitialResult.Initial -> {
                        previousState
                    }
                }

                is FetchEstimatesResult -> when (result) {
                    is FetchEstimatesResult.Loading -> {
                        previousState.copy(isLoading = true)
                    }
                    is FetchEstimatesResult.Error -> {
                        previousState.copy(isLoading = false, error = result.error)
                    }
                    is FetchEstimatesResult.Success -> {
                        previousState.copy(isLoading = false, estimates = result.estimates)
                    }
                }

            }
        }
    }
}
