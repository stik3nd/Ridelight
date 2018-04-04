package com.rdireito.ridelight

import com.rdireito.ridelight.common.di.component.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import javax.inject.Inject
import io.reactivex.exceptions.UndeliverableException
import java.io.IOException
import java.net.SocketException


class App : DaggerApplication() {

    @Inject lateinit var logger: Timber.Tree

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initMemoryAnalyzer()
    }

    private fun initMemoryAnalyzer() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }

    private fun initLogger() {
        Timber.plant(logger)
    }

    override fun applicationInjector(): AndroidInjector<out App> {
        return DaggerAppComponent.builder().create(this)
    }

}
