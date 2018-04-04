package com.rdireito.ridelight.feature.main.ui.activity

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.rdireito.ridelight.R
import com.rdireito.ridelight.common.architecture.BaseView
import com.rdireito.ridelight.common.ui.BaseActivity
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.databinding.ActivityMainBinding
import com.rdireito.ridelight.data.model.User
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivity
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : BaseActivity(), BaseView<MainUiIntent, MainUiState> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val confirmDropoffIntentPublisher = PublishSubject.create<MainUiIntent.ConfirmDropoffLoadIntent>()

    private val viewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders
            .of(this, viewModelFactory)
            .get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        val user = User("Rodrigo")
        binding.user = user
        binding.userNameText

        bind()
        startActivity(AddressSearchActivity.getIntent(this))
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun intents(): Observable<MainUiIntent> =
        Observable.merge(
            initialIntent(),
            confirmDropoffIntent()
        )


    override fun render(state: MainUiState) {
        loadingProgress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        errorText.visibility = if (state.error != null) View.VISIBLE else View.GONE
        state.error?.let {
            errorText.setText("Error: ${it.localizedMessage}")
            return
        }

        state
            .estimates
            .takeIf { it.isNotEmpty() }
            ?.map {
                Timber.d("vehicle=[${it.vehicleType.name}]")
            }
    }

    private fun bind() {
        viewModel
            .states()
            .subscribe(this::render)
            .disposeOnDestroy()
        viewModel.processIntents(intents())
    }

    private fun initialIntent(): Observable<MainUiIntent.InitialIntent> =
        Observable.just(MainUiIntent.InitialIntent)

    private fun confirmDropoffIntent(): Observable<MainUiIntent.ConfirmDropoffLoadIntent> {
        goButton.clicks()
            .throttleFirst(TAP_THROTTLE_TIME, TimeUnit.MILLISECONDS)
            .map { MainUiIntent.ConfirmDropoffLoadIntent(Address.ABSENT) }
            .subscribe(confirmDropoffIntentPublisher)
        return confirmDropoffIntentPublisher
    }

    companion object {
        private const val TAP_THROTTLE_TIME = 500L
    }

}
