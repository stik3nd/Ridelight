package com.rdireito.ridelight.feature.ride.ui

import android.arch.lifecycle.ViewModel
import arrow.syntax.option.some
import com.rdireito.ridelight.common.architecture.BaseViewModel
import com.rdireito.ridelight.feature.ride.ui.mvi.RideAction
import com.rdireito.ridelight.feature.ride.ui.mvi.RideAction.*
import com.rdireito.ridelight.feature.ride.ui.mvi.RideResult
import com.rdireito.ridelight.feature.ride.ui.mvi.RideResult.*
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiIntent
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiIntent.*
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiState
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject


class RideViewModel @Inject constructor(
    private val actionProcessorHolder: RideActionProcessor
) : ViewModel(), BaseViewModel<RideUiIntent, RideUiState> {

    private val intentsSubject = PublishSubject.create<RideUiIntent>()
    private val statesObservable: Observable<RideUiState> = stream()

    override fun processIntents(intents: Observable<RideUiIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<RideUiState> = statesObservable

    private fun stream(): Observable<RideUiState> {
        return intentsSubject
            .doOnNext { Timber.d("uiintent=[$it]") }
            .map(this::intentToAction)
            .doOnNext { Timber.d("action=[$it]") }
            .compose(actionProcessorHolder.actionProcessor)
            .doOnNext { Timber.d("result=[$it]") }
            .scan(RideUiState.idle(), reducer)
            .doOnNext { Timber.d("uistate=[$it]") }
            .distinctUntilChanged()
            .doOnNext { Timber.d("distinct=[$it]") }
            // Replay the last emitted item on subscription,
            // for when a View rebinds to the ViewModel after rotation.
            .replay(1)
            // Create the stream immediately, no subscribers needed.
            // This allows the stream to stay alive when the UI disconnects and
            // matches the stream lifecycle to the ViewModel's lifecycle.
            .autoConnect(0)
    }

    private fun intentToAction(intent: RideUiIntent): RideAction =
        when (intent) {
            is InitialIntent -> InitialAction
            is ChangeDropoffIntent -> InvokeChangeDropoffAction
            is ChangePickupIntent -> InvokeChangePickupAction
            is OnActivityResultIntent -> CheckActivityResultAction(intent.activityResult)
            is ConfirmDropoffIntent -> FetchEstimatesAction(intent.address)
            is ConfirmPickupIntent -> FetchEstimatesAction(intent.address)
        }

    companion object {
        const val DROPOFF_REQUEST_CODE = 1
        const val PICKUP_REQUEST_CODE = 2

        private val reducer = BiFunction { previousState: RideUiState, result: RideResult ->
            when (result) {
                is InitialResult -> when (result) {
                    is InitialResult.Initial -> {
                        previousState
                    }
                }

                is InvokeChangeDropoffResult.Invoke -> {
                    previousState.copy(invokeChangeDropoff = true)
                }

                is InvokeChangePickupResult.Invoke -> {
                    previousState.copy(invokeChangePickup = true)
                }

                is RideResult.CheckActivityResult -> when (result) {
                    is CheckActivityResult.Failure -> previousState.copy(
                        invokeChangeDropoff = false, invokeChangePickup = false
                    )
                    is CheckActivityResult.DropoffSuccess -> previousState.copy(
                        invokeChangeDropoff = false, dropoffAdress = result.address.some()
                    )
                    is CheckActivityResult.PickupSuccess -> previousState.copy(
                        invokeChangePickup = false, pickupAdress = result.address.some()
                    )
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
