package com.rdireito.ridelight.feature.addresssearch.ui

import android.arch.lifecycle.ViewModel
import com.rdireito.ridelight.common.architecture.BaseViewModel
import com.rdireito.ridelight.feature.addresssearch.mvi.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchAction.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchResult.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchResult.FetchAddressesResult.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchUiIntent.*
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


class AddressSearchViewModel @Inject constructor(
    private val actionProcessorHolder: AddressSearchActionProcessor
) : ViewModel(), BaseViewModel<AddressSearchUiIntent, AddressSearchUiState> {

    private val intentsSubject = PublishSubject.create<AddressSearchUiIntent>()
    private val statesObservable: Observable<AddressSearchUiState> = stream()

    override fun processIntents(intents: Observable<AddressSearchUiIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<AddressSearchUiState> = statesObservable

    private fun stream(): Observable<AddressSearchUiState> {
        return intentsSubject
            .map(this::intentToAction)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(AddressSearchUiState.idle(), reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private fun intentToAction(intent: AddressSearchUiIntent): AddressSearchAction =
        when (intent) {
            is ClearAddressIntent -> ClearAddressAction
            is ChangeAddressIntent -> FetchAddressesAction(intent.query)
            is SelectAddressIntent -> SelectAddressAction(intent.address)
        }

    companion object {
        private val reducer = BiFunction { previousState: AddressSearchUiState, result: AddressSearchResult ->
            when (result) {
                is ClearAddressResult -> when (result) {
                    is ClearAddressResult.Success -> {
                        AddressSearchUiState.idle()
                    }
                }

                is FetchAddressesResult -> when (result) {
                    is Loading -> previousState.loading()
                    is Error -> previousState.error(result.error)
                    is FetchAddressesResult.Success -> {
                        previousState.content(result.addresses)
                    }
                }

                is SelectAddressResult -> when (result) {
                    is SelectAddressResult.Success -> {
                        AddressSearchUiState.addressSelected(result.address)
                    }
                }
            }
        }
    }


}
