package com.rdireito.ridelight.feature.main.ui.activity

import android.os.Bundle
import android.widget.Toast
import com.rdireito.ridelight.R
import com.rdireito.ridelight.common.ui.BaseActivity
import javax.inject.Inject

class MainActivity : BaseActivity(), MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.onCreate()
    }

    override fun show(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
