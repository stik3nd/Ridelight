package com.rdireito.ridelight.common.di.module

import android.content.Context
import android.location.Geocoder
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.Reusable
import java.util.Locale
import javax.inject.Singleton

@Module
class AndroidModule {

    @Provides
    @Reusable
    fun providesLocale(context: Context): Locale = context.resources.configuration.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locales.get(0)
        } else {
            locale
        }
    }

    @Provides
    @Reusable
    fun providesGeocoder(context: Context, locale: Locale): Geocoder = Geocoder(
        context,
        locale
    )

}
