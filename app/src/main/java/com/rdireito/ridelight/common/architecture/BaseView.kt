package com.rdireito.ridelight.common.architecture

import io.reactivex.Observable

/**
 * The view should emit [BaseUiIntent]s and be able to render a [BaseUiState].
 */
interface BaseView<I : BaseUiIntent, in S : BaseUiState> {

    fun intents(): Observable<I>
    fun render(state: S)

}
