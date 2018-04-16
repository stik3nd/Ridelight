package com.rdireito.ridelight.feature.ride.ui

import android.arch.lifecycle.ViewModel
import android.location.Location
import arrow.core.Option
import arrow.core.applicative
import arrow.core.ev
import arrow.syntax.applicative.map
import arrow.syntax.option.none
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
import kotlin.properties.Delegates


class RideViewModel @Inject constructor(
    private val actionProcessorHolder: RideActionProcessor
) : ViewModel(), BaseViewModel<RideUiIntent, RideUiState> {

    private val intentsSubject = PublishSubject.create<RideUiIntent>()
    private val statesObservable: Observable<RideUiState> = stream()

    var currentPosition: Option<Location> by Delegates.vetoable(none()) { _, oldValue, newValue ->
        if (oldValue.isEmpty()) {
            true
        } else {
            // If any of the values is none() it short-circuits to none()
            val oldNewPair = Option.applicative().map(oldValue, newValue) { (old, new) ->
                Pair(old, new)
            }.ev()

            // If newValue accuracy is higher than oldValue we keep it, otherwise we veto the newValue
            oldNewPair.fold(
                none@{ false },
                some@{ (old, new) -> new.accuracy > old.accuracy }
            )
        }
    }

    override fun processIntents(intents: Observable<RideUiIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<RideUiState> = statesObservable

    private fun stream(): Observable<RideUiState> {
        return intentsSubject
            .doOnNext { Timber.d("uiintent=[$it]") }
            .map(this::intentToAction)
            .doOnNext { Timber.d("action=[$it]") }
            .filter { it != SkipAction }
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
            is InitialIntent -> InitialAction(intent.savedInstanceState)
            is ChangeDropoffIntent -> SkipAction
            is ChangePickupIntent -> SkipAction
            is OnActivityResultIntent -> CheckActivityResultAction(intent.eitherActivityResult)
            is ConfirmDropoffIntent -> ConfirmDropoffAction(intent.address, currentPosition)
            is ConfirmPickupIntent -> FetchEstimatesAction(intent.pickup, intent.dropoff)
            is ProductTryAgainIntent -> FetchEstimatesAction(intent.pickup, intent.dropoff)
            is NewLocationIntent -> {
                currentPosition = intent.location
                SkipAction
            }
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
                    is InitialResult.RestoreState -> {
                        result.restoredUiState
                    }
                }

                is CheckActivityResult -> when (result) {
                    is CheckActivityResult.Failure -> previousState
                    is CheckActivityResult.DropoffSuccess -> previousState.copy(
                            dropoffAdress = result.address
                    )
                    is CheckActivityResult.PickupSuccess -> previousState.copy(
                        pickupAdress = result.address
                    )
                }

                is ConfirmDropoffResult -> when (result) {
                    is ConfirmDropoffResult.Invalid -> previousState.copy(
                        showPickupFields = false, invalidAddress = true
                    )
                    is ConfirmDropoffResult.Valid -> previousState.copy(
                        showPickupFields = true, invalidAddress = false
                    )
                    is ConfirmDropoffResult.ValidWithPickup -> previousState.copy(
                            pickupAdress = result.initialPickup.orNull()
                    )
                    is ConfirmDropoffResult.HideMessage -> previousState.copy(
                        invalidAddress = false
                    )
                }

                is FetchEstimatesResult -> when (result) {
                    is FetchEstimatesResult.Loading -> previousState.copy(
                        isLoading = true, showProducts = true, error = null, estimates = emptyList()
                    )
                    is FetchEstimatesResult.Error -> previousState.copy(
                        isLoading = false, error = result.error, estimates = emptyList()
                    )
                    is FetchEstimatesResult.Success -> previousState.copy(
                        isLoading = false, estimates = result.estimates, error = null
                    )
                    is FetchEstimatesResult.InvalidParams -> previousState.copy(
                        invalidAddress = true
                    )
                    is FetchEstimatesResult.HideMessage -> previousState.copy(
                        invalidAddress = false
                    )
                }
            }
        }

    }

}
