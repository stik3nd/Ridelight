package com.rdireito.ridelight.common.architecture

import io.reactivex.Observable

/**
 * The model should process [BaseUiIntent]s coming from the [BaseView] and
 * with that generate new [BaseUiState]s, which the [BaseView] is able to render.
 */
interface BaseViewModel<I : BaseUiIntent, S : BaseUiState> {

    fun processIntents(intents: Observable<I>)
    fun states(): Observable<S>
}
