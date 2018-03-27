package com.rdireito.ridelight.feature.main.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.widget.Toast
import com.rdireito.ridelight.R
import com.rdireito.ridelight.common.ui.BaseActivity
import com.rdireito.ridelight.databinding.ActivityMainBinding
import com.rdireito.ridelight.data.model.User
import javax.inject.Inject

class MainActivity : BaseActivity(), MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_main)

        presenter.onCreate()
        val user = User("Rodrigo")
        binding.user = user
    }

    override fun show(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
