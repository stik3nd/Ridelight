package com.rdireito.ridelight.feature.addresssearch.ui

import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import com.rdireito.ridelight.data.repository.AddressRepository
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchAction
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchResult
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import javax.inject.Inject

class AddressSearchActionProcessor @Inject constructor(
    private val addressRepository: AddressRepository,
    private val scheduler: SchedulerComposer
) {

    var actionProcessor =
        ObservableTransformer<AddressSearchAction, AddressSearchResult> {
            ObservableSource { }
        }

}
