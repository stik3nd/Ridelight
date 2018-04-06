package com.rdireito.ridelight.feature.ride.ui

import com.google.gson.Gson
import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import com.rdireito.ridelight.common.ui.ActivityResult
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.model.request.EstimateRequest
import com.rdireito.ridelight.data.repository.EstimateRepository
import com.rdireito.ridelight.feature.ride.ui.mvi.RideAction
import com.rdireito.ridelight.feature.ride.ui.mvi.RideResult
import com.rdireito.ridelight.feature.ride.ui.RideViewModel.Companion.DROPOFF_REQUEST_CODE
import com.rdireito.ridelight.feature.ride.ui.RideViewModel.Companion.PICKUP_REQUEST_CODE
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivity.Companion.EXTRA_ADDRESS
import com.rdireito.ridelight.feature.ride.ui.mvi.RideAction.*
import com.rdireito.ridelight.feature.ride.ui.mvi.RideResult.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject

class RideActionProcessor @Inject constructor(
    private val estimateRepository: EstimateRepository,
    private val scheduler: SchedulerComposer,
    private val gson: Gson
) {

    private val estimateRequestJson =
        """{
  "stops": [
    {
      "loc": [
        40.4169473,
        -3.7057172
      ],
      "name": "Puerta del Sol",
      "addr": "Plaza de la Puerta del Sol",
      "num": "s/n",
      "city": "Madrid",
      "country": "Spain",
      "instr": "Hello, world!",
      "contact": {
        "name": "John Doe",
        "mobile_cc": "+34",
        "mobile_num": "611111113"
      }
    },
    {
      "loc": [
      40.415097,
      -3.713593
      ],
      "name": "Puerta del Sol",
      "addr": "Plaza de la Puerta del Sol",
      "num": "s/n",
      "city": "Madrid",
      "country": "Spain",
      "instr": "Hello, world!",
      "contact": {
        "name": "John Doe",
        "mobile_cc": "+34",
        "mobile_num": "611111113"
      }
    }
  ]
}"""

    private val estimateRequest = gson.fromJson<EstimateRequest>(estimateRequestJson, EstimateRequest::class.java)

    private val initialActionProcessor =
        ObservableTransformer<RideAction.InitialAction, RideResult.InitialResult> { actions ->
            actions.flatMap { action ->
                Observable
                    .just(RideResult.InitialResult.Initial)
            }
        }

    private val invokeChangeDropoffActionProcessor =
        ObservableTransformer<InvokeChangeDropoffAction, InvokeChangeDropoffResult> { actions ->
            actions.flatMap { _ ->
                Observable.just(InvokeChangeDropoffResult.Invoke)
            }
        }

    private val invokeChangePickupActionProcessor =
        ObservableTransformer<InvokeChangePickupAction, InvokeChangePickupResult> { actions ->
            actions.flatMap { _ ->
                Observable.just(InvokeChangePickupResult.Invoke)
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


    private val fetchEstimatesProcessor =
        ObservableTransformer<RideAction.FetchEstimatesAction, RideResult.FetchEstimatesResult> { actions ->
            actions.flatMap { action ->
                estimateRepository.estimates(estimateRequest)
                    .toObservable()
                    .map { estimates -> RideResult.FetchEstimatesResult.Success(estimates) }
                    .cast(RideResult.FetchEstimatesResult::class.java)
                    .onErrorReturn(RideResult.FetchEstimatesResult::Error)
                    .subscribeOn(scheduler.io())
                    .observeOn(scheduler.ui())
                    .startWith(RideResult.FetchEstimatesResult.Loading)
            }
        }

    private fun matchActionsToProcessors(selector: Observable<RideAction>): Observable<RideResult> =
        Observable.merge(listOf(
            selector.ofType(InitialAction::class.java).compose(initialActionProcessor),
            selector.ofType(InvokeChangeDropoffAction::class.java).compose(invokeChangeDropoffActionProcessor),
            selector.ofType(InvokeChangePickupAction::class.java).compose(invokeChangePickupActionProcessor),
            selector.ofType(CheckActivityResultAction::class.java).compose(checkActivityResultActionProcessor),
            selector.ofType(FetchEstimatesAction::class.java).compose(fetchEstimatesProcessor)
        ))

    private fun assertActionIsImplemented(selector: Observable<RideAction>): Observable<RideResult> =
        selector.filter { v ->
            v !is InitialAction
                && v !is InvokeChangeDropoffAction
                && v !is InvokeChangePickupAction
                && v !is CheckActivityResultAction
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
