package com.rdireito.ridelight.feature.addresssearch.ui

import android.arch.lifecycle.ViewModel
import com.rdireito.ridelight.common.architecture.BaseViewModel
import com.rdireito.ridelight.feature.addresssearch.mvi.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchAction.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchResult.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchResult.AllowClearAddressResult.Allow
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchResult.AllowClearAddressResult.Deny
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchResult.FetchAddressesResult.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchUiIntent.*
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
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
            .doOnNext { Timber.d("uiintent=[$it]") }
            .map(this::intentToAction)
            .doOnNext { Timber.d("action=[$it]") }
            .compose(actionProcessorHolder.actionProcessor)
            .doOnNext { Timber.d("result=[$it]") }
            .scan(AddressSearchUiState.idle(), reducer)
            .doOnNext { Timber.d("uistate=[$it]") }
            .distinctUntilChanged()
            .doOnNext { Timber.d("distinct=[$it]") }
            .replay(1)
            .autoConnect(0)
    }

    private fun intentToAction(intent: AddressSearchUiIntent): AddressSearchAction =
        when (intent) {
            is ClearAddressIntent -> ClearAddressAction
            is InputSearchTextIntent -> AllowClearAddressAction(intent.length)
            is ChangeAddressIntent -> FetchAddressesAction(intent.query)
            is SelectAddressIntent -> SelectAddressAction(intent.address)
        }

    companion object {
        private val reducer = BiFunction { previousState: AddressSearchUiState, result: AddressSearchResult ->
            when (result) {
                is ClearAddressResult.Success ->
                    AddressSearchUiState.idle().copy(clearQuery = true)

                is AllowClearAddressResult -> when (result) {
                    is Allow -> previousState.copy(clearQuery = false, hasClearButton = true)
                    is Deny -> previousState.copy(clearQuery = false, hasClearButton = false, addresses = emptyList())
                }

                is FetchAddressesResult -> when (result) {
                    is Loading -> previousState.loading()
                    is Error -> previousState.error(result.error)
                    is FetchAddressesResult.Success -> {
                        previousState.content(result.addresses)
                    }
                }

                is SelectAddressResult.Success ->
                    AddressSearchUiState.addressSelected(result.address)
            }
        }
    }


}
