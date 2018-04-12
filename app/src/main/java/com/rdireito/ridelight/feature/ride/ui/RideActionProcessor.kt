package com.rdireito.ridelight.feature.ride.ui

import arrow.core.Option
import arrow.core.applicative
import arrow.core.ev
import arrow.syntax.applicative.map
import arrow.syntax.option.none
import arrow.syntax.option.some
import arrow.syntax.option.toOption
import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import com.rdireito.ridelight.common.ui.ActivityResult
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.model.request.EstimateRequest
import com.rdireito.ridelight.data.repository.AddressRepository
import com.rdireito.ridelight.data.repository.EstimateRepository
import com.rdireito.ridelight.feature.MESSAGE_TIME
import com.rdireito.ridelight.feature.ride.ui.mvi.RideAction
import com.rdireito.ridelight.feature.ride.ui.mvi.RideResult
import com.rdireito.ridelight.feature.ride.ui.RideViewModel.Companion.DROPOFF_REQUEST_CODE
import com.rdireito.ridelight.feature.ride.ui.RideViewModel.Companion.PICKUP_REQUEST_CODE
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivity.Companion.EXTRA_ADDRESS
import com.rdireito.ridelight.feature.ride.ui.mvi.RideAction.*
import com.rdireito.ridelight.feature.ride.ui.mvi.RideResult.*
import com.rdireito.ridelight.feature.ride.ui.mvi.RideResult.FetchEstimatesResult.*
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RideActionProcessor @Inject constructor(
    private val estimateRepository: EstimateRepository,
    private val addressRepository: AddressRepository,
    private val scheduler: SchedulerComposer
) {

    private val initialActionProcessor =
        ObservableTransformer<RideAction.InitialAction, RideResult.InitialResult> { actions ->
            actions.flatMap { _ ->
                Observable
                    .just(RideResult.InitialResult.Initial)
            }
        }

    private val invokeChangeDropoffActionProcessor =
        ObservableTransformer<InvokeChangeDropoffAction, InvokeChangeDropoffResult> { actions ->
            actions.map { _ ->
                InvokeChangeDropoffResult.Invoke
            }
        }

    private val invokeChangePickupActionProcessor =
        ObservableTransformer<InvokeChangePickupAction, InvokeChangePickupResult> { actions ->
            actions.map { _ ->
                InvokeChangePickupResult.Invoke
            }
        }

    private val checkActivityResultActionProcessor =
        ObservableTransformer<CheckActivityResultAction, CheckActivityResult> { actions ->
            actions.flatMap { action ->
                Observable.fromCallable<CheckActivityResult> {
                    try {
                        val activityResult = action.activityResult
                        when (activityResult) {
                            is ActivityResult.SuccessWithData -> {
                                when (activityResult.requestCode) {
                                    DROPOFF_REQUEST_CODE -> CheckActivityResult.DropoffSuccess(
                                        activityResult.data[EXTRA_ADDRESS] as Address
                                    )

                                    PICKUP_REQUEST_CODE -> CheckActivityResult.PickupSuccess(
                                        activityResult.data[EXTRA_ADDRESS] as Address
                                    )

                                    else -> CheckActivityResult.Failure(
                                        IllegalArgumentException("Unknown request code=[${activityResult.requestCode}]")
                                    )
                                }
                            }

                            is ActivityResult.FailureWithData -> CheckActivityResult.Failure(
                                Throwable("Result canceled")
                            )
                        }
                    } catch (e: ClassCastException) {
                        CheckActivityResult.Failure(e)
                    }
                }
            }
        }

    private val confirmDropoffActionProcessor =
        ObservableTransformer<ConfirmDropoffAction, ConfirmDropoffResult> { actions ->
            actions.flatMap { action ->
                action.dropoffAddress.fold(
                    none@{
                        Observable.timer(MESSAGE_TIME, TimeUnit.MILLISECONDS)
                            .map { ConfirmDropoffResult.HideMessage }
                            .cast(ConfirmDropoffResult::class.java)
                            .startWith(ConfirmDropoffResult.Invalid)
                    },
                    some@{ _ ->
                        action.currentPosition.fold(
                            none@{ Observable.just(ConfirmDropoffResult.Valid) },
                            some@{ userPosition ->
                                addressRepository
                                    // Try to get an pickup address based on the current best position we have from the user,
                                    // if we don't find any address just return a none()
                                    .addressByLocation(userPosition.latitude, userPosition.longitude)
                                    .toObservable()
                                    .map { address -> ConfirmDropoffResult.ValidWithPickup(address.some()) }
                                    .defaultIfEmpty(ConfirmDropoffResult.ValidWithPickup(none()))
                                    .onErrorReturn { ConfirmDropoffResult.ValidWithPickup(none()) }
                                    .cast(ConfirmDropoffResult::class.java)
                                    .subscribeOn(scheduler.io())
                                    .observeOn(scheduler.ui())
                                    // Start with a Valid confirm drop off so our UI will render this state
                                    .startWith(ConfirmDropoffResult.Valid)
                            }
                        )
                    }
                )
            }
        }


    private val fetchEstimatesProcessor =
        ObservableTransformer<RideAction.FetchEstimatesAction, RideResult.FetchEstimatesResult> { actions ->
            actions.flatMap { action ->
                // If any of the values is none() it short-circuits to none()
                val estimateRequest = Option.applicative().map(action.pickup, action.dropoff, { (pickup, dropoff) ->
                    EstimateRequest(pickup, dropoff)
                }).ev()

                estimateRequest.fold(
                    none@ {
                        Observable.timer(MESSAGE_TIME, TimeUnit.MILLISECONDS)
                            .map { FetchEstimatesResult.HideMessage }
                            .cast(FetchEstimatesResult::class.java)
                            .startWith(FetchEstimatesResult.InvalidParams)
                    },
                    some@ { request ->
                        estimateRepository.estimates(request)
                            .toObservable()
                            .map { estimates -> Success(estimates) }
                            .cast(FetchEstimatesResult::class.java)
                            .onErrorReturn(FetchEstimatesResult::Error)
                            .subscribeOn(scheduler.io())
                            .observeOn(scheduler.ui())
                            .startWith(FetchEstimatesResult.Loading)
                    }
                )

            }
        }

    private fun matchActionsToProcessors(selector: Observable<RideAction>): Observable<RideResult> =
        Observable.merge(listOf(
            selector.ofType(InitialAction::class.java).compose(initialActionProcessor),
            selector.ofType(InvokeChangeDropoffAction::class.java).compose(invokeChangeDropoffActionProcessor),
            selector.ofType(InvokeChangePickupAction::class.java).compose(invokeChangePickupActionProcessor),
            selector.ofType(CheckActivityResultAction::class.java).compose(checkActivityResultActionProcessor),
            selector.ofType(ConfirmDropoffAction::class.java).compose(confirmDropoffActionProcessor),
            selector.ofType(FetchEstimatesAction::class.java).compose(fetchEstimatesProcessor)
        ))

    private fun assertActionIsImplemented(selector: Observable<RideAction>): Observable<RideResult> =
        selector.filter { v ->
            v !is InitialAction
                && v !is InvokeChangeDropoffAction
                && v !is InvokeChangePickupAction
                && v !is CheckActivityResultAction
                && v !is ConfirmDropoffAction
                && v !is FetchEstimatesAction
        }.flatMap { v ->
            Observable.error<RideResult>(
                IllegalArgumentException("Unknown Action type=[$v]")
            )
        }

    var actionProcessor =
        ObservableTransformer<RideAction, RideResult> { actions ->
            actions.publish { selector ->
                matchActionsToProcessors(selector)
                    .mergeWith(
                        assertActionIsImplemented(selector)
                    )
            }
        }
}
