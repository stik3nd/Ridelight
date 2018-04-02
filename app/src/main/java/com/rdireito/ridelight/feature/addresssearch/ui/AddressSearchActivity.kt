package com.rdireito.ridelight.feature.addresssearch.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jakewharton.rxbinding2.view.clicks
import com.rdireito.ridelight.R
import com.rdireito.ridelight.common.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_address_search.*

class AddressSearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_search)
        addressSearchBackImage.clicks().subscribe { finish() }.disposeOnDestroy()
    }


    companion object {
        fun getIntent(context: Context) =
            Intent(context, AddressSearchActivity::class.java)
    }


}
