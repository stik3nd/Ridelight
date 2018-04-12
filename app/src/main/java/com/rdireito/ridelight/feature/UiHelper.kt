package com.rdireito.ridelight.feature

import io.reactivex.ObservableTransformer
import java.util.concurrent.TimeUnit

const val TAP_THROTTLE_TIME = 500L // milliseconds
const val MESSAGE_TIME = 3500L // milliseconds

fun <T> userEventLimiter(): ObservableTransformer<T, T> {
    return ObservableTransformer { upstream ->
        upstream.throttleFirst(TAP_THROTTLE_TIME, TimeUnit.MILLISECONDS)
    }
}