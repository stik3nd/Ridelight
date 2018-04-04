package com.rdireito.ridelight.feature.addresssearch.ui

import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import com.rdireito.ridelight.data.repository.AddressRepository
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchAction
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchAction.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchResult
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchResult.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchResult.AllowClearAddressResult.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject

class AddressSearchActionProcessor @Inject constructor(
    private val addressRepository: AddressRepository,
    private val scheduler: SchedulerComposer
) {

    private val clearAddressActionProcessor =
        ObservableTransformer<ClearAddressAction, ClearAddressResult> { actions ->
            actions.flatMap { _ ->
                Observable.just(ClearAddressResult.Success)
            }
        }

    private val allowClearAddressActionProcessor =
        ObservableTransformer<AllowClearAddressAction, AllowClearAddressResult> { actions ->
            actions.flatMap { clearAction ->
                Observable.fromCallable {
                    if (clearAction.length == 0) Deny else Allow
                }
            }
        }

    private val fetchAddressesActionProcessor =
        ObservableTransformer<FetchAddressesAction, FetchAddressesResult> { actions ->
            actions.flatMap { action ->
                addressRepository.addresses(action.query)
                    .toObservable()
                    .map(FetchAddressesResult::Success)
                    .cast(FetchAddressesResult::class.java)
                    .onErrorReturn(FetchAddressesResult::Error)
                    .subscribeOn(scheduler.io())
                    .observeOn(scheduler.ui())
                    .startWith(FetchAddressesResult.Loading)
            }
        }

    private val selectAddressActionProcessor =
        ObservableTransformer<SelectAddressAction, SelectAddressResult> { actions ->
            actions.flatMap { action ->
                Observable.just(SelectAddressResult.Success(action.address))
            }
        }

    var actionProcessor =
        ObservableTransformer<AddressSearchAction, AddressSearchResult> { actions ->
            actions.publish { selector ->
                matchActionsToProcessors(selector)
                    .mergeWith(
                        checkActionIsImplemented(selector)
                    )
            }
        }

    private fun matchActionsToProcessors(selector: Observable<AddressSearchAction>): Observable<AddressSearchResult> =
        Observable.merge(
            selector.ofType(ClearAddressAction::class.java).compose(clearAddressActionProcessor),
            selector.ofType(AllowClearAddressAction::class.java).compose(allowClearAddressActionProcessor),
            selector.ofType(FetchAddressesAction::class.java).compose(fetchAddressesActionProcessor),
            selector.ofType(SelectAddressAction::class.java).compose(selectAddressActionProcessor)
        )

    private fun checkActionIsImplemented(selector: Observable<AddressSearchAction>): Observable<AddressSearchResult> =
        selector.filter { v ->
            v !is ClearAddressAction
                && v !is AllowClearAddressAction
                && v !is FetchAddressesAction
                && v !is SelectAddressAction
        }
            .flatMap { v ->
                Observable.error<AddressSearchResult>(
                    IllegalArgumentException("Unknown Action type: $v")
                )
            }

}
