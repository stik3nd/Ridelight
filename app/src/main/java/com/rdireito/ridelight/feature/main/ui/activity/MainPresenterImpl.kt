package com.rdireito.ridelight.feature.main.ui.activity

import android.util.Log
import com.google.gson.Gson
import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import com.rdireito.ridelight.data.model.request.EstimateRequest
import com.rdireito.ridelight.data.repository.EstimateRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainPresenterImpl(
        private val view: MainContract.View,
        private val estimateRepository: EstimateRepository,
        private val scheduler: SchedulerComposer,
        private val gson: Gson
) : MainContract.Presenter {

    companion object {
        private val TAG: String = MainPresenterImpl::class.java.simpleName
    }

    override fun onCreate() {
        val estimateRequestJson =
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

        val estimateRequest = gson.fromJson<EstimateRequest>(estimateRequestJson, EstimateRequest::class.java)

        estimateRepository
                .estimates(estimateRequest)
                .subscribeOn(scheduler.io())
                .observeOn(scheduler.ui())
                .doOnSuccess {
                    it.map {
                        Log.d(TAG, "vehicle=[${it.vehicleType.name}]")
                    }
                }
                .doOnError {
                    Log.e(TAG, it.localizedMessage)
                }
                .subscribe()
    }

}
