package com.rdireito.ridelight.common.extension

import android.os.Build
import java.util.Locale

val Locale.languageTag: String
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> this.toLanguageTag()
        else -> this.isO3Language
    }
