package com.rdireito.ridelight.common.di

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner
import com.rdireito.ridelight.TestApp

class AppJUnitRunner : AndroidJUnitRunner() {
    override fun newApplication(
            cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}
