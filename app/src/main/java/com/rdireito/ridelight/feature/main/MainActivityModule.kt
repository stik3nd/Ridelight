package com.rdireito.ridelight.feature.main

import com.google.gson.Gson
import com.rdireito.ridelight.common.data.executor.SchedulerComposer
import com.rdireito.ridelight.common.di.scope.ActivityScope
import com.rdireito.ridelight.data.repository.EstimateRepository
import com.rdireito.ridelight.feature.main.ui.activity.MainActivity
import com.rdireito.ridelight.feature.main.ui.activity.MainContract
import com.rdireito.ridelight.feature.main.ui.activity.MainPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    @ActivityScope
    fun providesMainView(mainActivity: MainActivity): MainContract.View {
        return mainActivity
    }

    @Provides
    @ActivityScope
    fun providesMainPresenter(
            mainView: MainContract.View,
            estimateRepository: EstimateRepository,
            scheduler: SchedulerComposer,
            gson: Gson
    ): MainContract.Presenter =
            MainPresenterImpl(mainView, estimateRepository, scheduler, gson)

}
