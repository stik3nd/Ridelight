package com.rdireito.ridelight.feature.main.ui.activity

class MainPresenterImpl(
        val view: MainContract.View
) : MainContract.Presenter {

    override fun onCreate() {
        view.show("It works!")
    }

}
