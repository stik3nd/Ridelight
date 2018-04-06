package com.rdireito.ridelight.common.extension

import android.os.Bundle
import java.util.HashMap

fun Bundle.toMap(): Map<String, Any> {
    val results = HashMap<String, Any>(this.size())
    this.keySet().map { key ->
        results[key] = this.get(key)
    }
    return results
}