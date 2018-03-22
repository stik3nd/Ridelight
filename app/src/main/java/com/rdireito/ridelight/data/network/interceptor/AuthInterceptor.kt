package com.rdireito.ridelight.data.network.interceptor

import com.rdireito.ridelight.BuildConfig
import com.rdireito.ridelight.common.extension.languageTag
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
        private val locale: Locale
) : Interceptor {

    private val acceptLanguage: String by lazy { locale.languageTag }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(handleRequest(chain.request()))
    }

    private fun handleRequest(request: Request): Request = request
            .newBuilder()
            .apply {
                addHeader("Authorization", "Bearer ${BuildConfig.AUTH_TOKEN}")
                addHeader("Accept-Language", acceptLanguage)
            }
            .build()
}
