package com.rdireito.ridelight.feature.main

import com.rdireito.ridelight.common.di.scope.ActivityScope
import com.rdireito.ridelight.feature.main.ui.activity.MainActivity
import com.rdireito.ridelight.feature.main.ui.activity.MainContract
import com.rdireito.ridelight.feature.main.ui.activity.MainPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @ActivityScope
    @Provides
    fun providesMainView(mainActivity: MainActivity): MainContract.View {
        return mainActivity
    }

    @ActivityScope
    @Provides
    fun providesMainPresenter(mainView: MainContract.View): MainContract.Presenter {
        return MainPresenterImpl(mainView)
    }

}
