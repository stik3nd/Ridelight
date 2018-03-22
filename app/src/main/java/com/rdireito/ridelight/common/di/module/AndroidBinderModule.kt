package com.rdireito.ridelight.common.di.module

import com.rdireito.ridelight.feature.main.ui.activity.MainActivity
import com.rdireito.ridelight.common.di.scope.ActivityScope
import com.rdireito.ridelight.feature.main.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AndroidBinderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindsMainActivity(): MainActivity

}
