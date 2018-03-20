package com.rdireito.ridelight

import android.app.Application
import com.rdireito.ridelight.common.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class App : DaggerApplication() {

  override fun onCreate() {
    super.onCreate()
  }

  override fun applicationInjector(): AndroidInjector<out App> {
    return DaggerAppComponent.builder().create(this)
  }

}
