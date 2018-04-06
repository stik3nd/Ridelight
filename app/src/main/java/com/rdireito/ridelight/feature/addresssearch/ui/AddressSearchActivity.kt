package com.rdireito.ridelight.feature.addresssearch.ui

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.transition.TransitionManager
import android.view.View
import android.view.inputmethod.EditorInfo
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.editorActionEvents
import com.jakewharton.rxbinding2.widget.textChanges
import com.rdireito.ridelight.R
import com.rdireito.ridelight.common.architecture.BaseView
import com.rdireito.ridelight.common.ui.BaseActivity
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.model.Location
import com.rdireito.ridelight.feature.TAP_THROTTLE_TIME
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchUiIntent
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchUiIntent.*
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchUiState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_address_search.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddressSearchActivity : BaseActivity(), BaseView<AddressSearchUiIntent, AddressSearchUiState> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: AddressSearchViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders
            .of(this, viewModelFactory)
            .get(AddressSearchViewModel::class.java)
    }

    private val clearAddressIntentPublisher = PublishSubject.create<ClearAddressIntent>()
    private val inputSearchTextIntentPublisher = PublishSubject.create<InputSearchTextIntent>()
    private val changeAddressIntentPublisher = PublishSubject.create<ChangeAddressIntent>()
    private val selectAddressIntentPublisher = PublishSubject.create<SelectAddressIntent>()

    private val addressSearchAdapter: AddressSearchAdapter = AddressSearchAdapter { item, _ ->
        selectAddressIntentPublisher.onNext(SelectAddressIntent(item))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_search)

        init()
        bind()
    }

    override fun intents(): Observable<AddressSearchUiIntent> =
        Observable.merge(
            clearAddressIntent(),
            inputSearchTextIntent(),
            changeAddressIntent(),
            selectAddressIntent()
        )

    override fun render(state: AddressSearchUiState) {
        TransitionManager.beginDelayedTransition(root)

        addressSearchProgress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        addressSearchClearImage.visibility = if (state.hasClearButton) View.VISIBLE else View.GONE
        addressSearchErrorText.visibility = if (state.error != null) View.VISIBLE else View.GONE

        state.error?.let {
            addressSearchAdapter.clear()
            addressSearchErrorText.text = "Error: ${it.localizedMessage}"
            return
        }

        if (state.addresses.isEmpty()) {
            addressSearchAdapter.clear()
        } else {
            addressSearchAdapter.clear()
            addressSearchAdapter.addAll(state.addresses)
        }


        state.selectedAddress.map(this::finishWithAddress)

        if (state.clearQuery) {
            addressSearchAddressEdit.text.clear()
        }
    }

    private fun init() {
        addressSearchBackImage.clicks().subscribe { finish() }.disposeOnDestroy()

        addressSearchPlacesList.itemAnimator = DefaultItemAnimator()
        addressSearchPlacesList.adapter = addressSearchAdapter
    }

    private fun bind() {
        viewModel
            .states()
            .subscribe(this::render)
            .disposeOnDestroy()
        viewModel.processIntents(intents())

        intent.extras?.let {
            val address = it.getParcelable<Address>(EXTRA_ADDRESS)?.let {
                addressSearchAddressEdit.setText(it.address)
            }
        }

//        setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_ADDRESS,
//            Address("", "My address test, 99", "", "", "", Location(0.0, 0.0))
//            ))
//        finish()
    }

    private fun clearAddressIntent(): Observable<ClearAddressIntent> {
        addressSearchClearImage.clicks()
            .throttleFirst(TAP_THROTTLE_TIME, TimeUnit.MILLISECONDS)
            .map { ClearAddressIntent }
            .subscribe(clearAddressIntentPublisher)
        return clearAddressIntentPublisher
    }

    private fun inputSearchTextIntent(): Observable<InputSearchTextIntent> {
        addressSearchAddressEdit.textChanges()
            .map(CharSequence::length)
            .map(::InputSearchTextIntent)
            .subscribe(inputSearchTextIntentPublisher)
        return inputSearchTextIntentPublisher
    }

    private fun changeAddressIntent(): Observable<ChangeAddressIntent> {
        Observable.merge(
            addressSearchAddressEdit.editorActionEvents()
                .filter { it.actionId() == EditorInfo.IME_ACTION_SEARCH }
                .map { addressSearchAddressEdit.text.toString() },
            addressSearchAddressEdit.textChanges()
                .map(CharSequence::toString)

        )
            .filter { query -> query.length >= 3 }
            .debounce(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map(::ChangeAddressIntent)
            .subscribe(changeAddressIntentPublisher)
        return changeAddressIntentPublisher
    }

    private fun selectAddressIntent(): Observable<SelectAddressIntent> {
        return selectAddressIntentPublisher
    }

    private fun finishWithAddress(address: Address) {
        setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_ADDRESS, address))
        finish()
    }

    companion object {
        const val EXTRA_ADDRESS = "extra_address"

        fun getIntent(context: Context, address: Address? = null): Intent =
            Intent(context, AddressSearchActivity::class.java)
                .putExtra(EXTRA_ADDRESS, address)
    }

}
