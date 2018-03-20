package com.rdireito.ridelight.feature.main.ui.activity

object MainContract {

    interface View {
        fun show(text: String)
    }

    interface Presenter {
        fun onCreate()
    }

}