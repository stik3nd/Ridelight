package com.rdireito.ridelight.feature.main.ui.activity

import com.google.gson.Gson
import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import com.rdireito.ridelight.data.model.request.EstimateRequest
import com.rdireito.ridelight.data.repository.EstimateRepository
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject

class MainActionProcessor @Inject constructor(
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
        ObservableTransformer<MainAction.InitialAction, MainResult.InitialResult> { actions ->
            actions.flatMap { action ->
                Observable
                    .just(MainResult.InitialResult.Initial)
                    .cast(MainResult.InitialResult::class.java)
            }
        }

    private val fetchEstimatesProcessor =
        ObservableTransformer<MainAction.FetchEstimatesAction, MainResult.FetchEstimatesResult> { actions ->
            actions.flatMap { action ->
                estimateRepository.estimates(estimateRequest)
                    .toObservable()
                    .map { estimates -> MainResult.FetchEstimatesResult.Success(estimates) }
                    .cast(MainResult.FetchEstimatesResult::class.java)
                    .onErrorReturn(MainResult.FetchEstimatesResult::Error)
                    .subscribeOn(scheduler.io())
                    .observeOn(scheduler.ui())
                    .startWith(MainResult.FetchEstimatesResult.Loading)
            }
        }

    var actionProcessor =
        ObservableTransformer<MainAction, MainResult> { actions ->
            actions.publish { shared ->
                Observable.merge(
                    shared.ofType(MainAction.InitialAction::class.java).compose(initialActionProcessor),
                    shared.ofType(MainAction.FetchEstimatesAction::class.java).compose(fetchEstimatesProcessor)
                )
                    .mergeWith(
                        // Error for not implemented actions
                        shared.filter { v ->
                            v !is MainAction.InitialAction
                                && v !is MainAction.FetchEstimatesAction
                        }.flatMap { w ->
                            Observable.error<MainResult>(
                                IllegalArgumentException("Unknown Action type: $w"))
                        }
                    )
            }
        }
}
