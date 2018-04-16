package com.rdireito.ridelight.feature.ride.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.StringRes
import android.support.constraint.ConstraintSet
import android.support.design.widget.Snackbar
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import arrow.core.Either
import arrow.core.Either.Companion.left
import arrow.syntax.either.left
import arrow.syntax.either.right
import arrow.syntax.option.some
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.rdireito.ridelight.R
import com.rdireito.ridelight.common.architecture.BaseView
import com.rdireito.ridelight.common.extension.*
import com.rdireito.ridelight.common.ui.ActivityResult
import com.rdireito.ridelight.common.ui.ActivityResult.FailureWithData
import com.rdireito.ridelight.common.ui.ActivityResult.SuccessWithData
import com.rdireito.ridelight.common.ui.BaseActivity
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.source.BoundLocationManager
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivity
import com.rdireito.ridelight.feature.ride.ui.RideViewModel.Companion.DROPOFF_REQUEST_CODE
import com.rdireito.ridelight.feature.ride.ui.RideViewModel.Companion.PICKUP_REQUEST_CODE
import com.rdireito.ridelight.feature.ride.ui.map.RideMapFragment
import com.rdireito.ridelight.feature.ride.ui.map.RideMapFragment.*
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiIntent
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiIntent.*
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiState
import com.rdireito.ridelight.feature.userEventLimiter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_ride.*
import timber.log.Timber
import javax.inject.Inject

class RideActivity : BaseActivity(), BaseView<RideUiIntent, RideUiState> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: RideViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders
            .of(this, viewModelFactory)
            .get(RideViewModel::class.java)
    }

    private val changeDropoffIntentPublisher = PublishSubject.create<ChangeDropoffIntent>()
    private val changePickupIntentPublisher = PublishSubject.create<ChangePickupIntent>()
    private val confirmDropoffIntentPublisher = PublishSubject.create<ConfirmDropoffIntent>()
    private val onActivityResultPublisher = PublishSubject.create<OnActivityResultIntent>()
    private val confirmPickupIntentPublisher = PublishSubject.create<ConfirmPickupIntent>()
    private val productTryAgainIntentPublisher = PublishSubject.create<ProductTryAgainIntent>()
    private val newLocationIntentPublisher = PublishSubject.create<NewLocationIntent>()
    private lateinit var initialIntentObservable: Observable<InitialIntent>

    private val rxPermissions: RxPermissions by lazy { RxPermissions(this) }
    private val boundLocationCallback: BoundLocationCallback = BoundLocationCallback(newLocationIntentPublisher)
    private val locationManager: BoundLocationManager = BoundLocationManager(this, this, boundLocationCallback)
    private var mapFragment: RideMapFragment? = null
        get() = findFragmentWithType(RideMapFragment.TAG)
    private var rideAddressViewSubscription = Disposables.empty()
    private var snackbar: Snackbar? = null
    private var uiState: RideUiState = RideUiState.idle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride)

        init(savedInstanceState)
        bind()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val resultData = (data?.extras ?: Bundle.EMPTY).toMap()
        val eitherActivityResult: Either<FailureWithData, SuccessWithData> =
            if (resultCode == Activity.RESULT_OK) {
                SuccessWithData(requestCode, resultData).right()
            } else {
                FailureWithData(requestCode, resultData).left()
            }
        onActivityResultPublisher.onNext(OnActivityResultIntent(eitherActivityResult))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable("state", uiState)
        super.onSaveInstanceState(outState)
    }

    override fun intents(): Observable<RideUiIntent> =
        Observable.merge(listOf(
            initialIntent(),
            newLocationIntent(),
            changeDropoffIntent(),
            changePickupIntent(),
            onActivityResultIntent(),
            confirmDropoffIntent(),
            confirmPickupIntent(),
            productTryAgainIntent()
        ))

    override fun render(state: RideUiState) {
        uiState = state

        if (state.invalidAddress) {
            showMessage(R.string.select_address)
        } else {
            hideMessage()
        }

        // renderDropoffState
        state.dropoffAdress?.let { renderDropoff(it) }

        // renderPickupState
        if (state.showPickupFields) {
            rideAddressView.showPickupFields()
        }
        state.pickupAdress?.let { renderPickup(it) }

        // renderProductsState
        if (state.showProducts) {
            showProductsView()

            rideProductView.run {
                setLoading(state.isLoading)
                setError(state.error)
                setProducts(state.estimates)
            }
        } else {
            rideProductView.visibility = View.GONE
        }
    }

    private fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState == null)
            attachViews()

        requestLocationPermission()
        initialIntentObservable = Observable.just(InitialIntent(savedInstanceState))
        subscribeToAddressHeightChanges()
    }

    private fun attachViews() {
        supportFragmentManager.commitTransactions {
            it.attachFragment(RideMapFragment.newInstance(), R.id.rideMapContainer, RideMapFragment.TAG)
        }
    }

    private fun requestLocationPermission() {
        rxPermissions
            .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        Timber.d("permissionResult:granted=[${permission.granted}]")
                    }

                    permission.shouldShowRequestPermissionRationale -> {
                        AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setMessage(R.string.location_permission_rationale)
                            .setPositiveButton(R.string.ok) { _, _ -> requestLocationPermission() }
                            .show()
                    }

                    else -> {
                        // Denied permission with never ask again
                        AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setMessage(R.string.location_permission_never_ask_again)
                            .setPositiveButton(R.string.ok) { _, _ -> openAppSettingsDetails() }
                            .setNegativeButton(R.string.no) { _, _ -> finish() }
                            .show()
                    }
                }
            }
            .disposeOnDestroy()
    }

    private fun bind() {
        viewModel
            .states()
            .subscribe(this::render)
            .disposeOnDestroy()
        viewModel.processIntents(intents())
    }

    private fun initialIntent(): Observable<InitialIntent> =
        initialIntentObservable

    private fun newLocationIntent(): Observable<NewLocationIntent> {
        return newLocationIntentPublisher
    }

    private fun changeDropoffIntent(): Observable<ChangeDropoffIntent> {
        rideAddressView.dropoffClicks()
            .compose(userEventLimiter())
            .map { ChangeDropoffIntent }
            .doOnNext {
                startActivityForResult(AddressSearchActivity.getIntent(
                    this, rideAddressView.dropoffAdress.orNull()), DROPOFF_REQUEST_CODE
                )
            }
            .subscribe(changeDropoffIntentPublisher)
        return changeDropoffIntentPublisher
    }

    private fun changePickupIntent(): Observable<ChangePickupIntent> {
        rideAddressView.pickupClicks()
            .compose(userEventLimiter())
            .map { ChangePickupIntent }
            .doOnNext {
                startActivityForResult(AddressSearchActivity.getIntent(
                    this, rideAddressView.pickupAdress.orNull()), PICKUP_REQUEST_CODE
                )
            }
            .subscribe(changePickupIntentPublisher)
        return changePickupIntentPublisher
    }

    private fun onActivityResultIntent(): Observable<OnActivityResultIntent> =
        onActivityResultPublisher

    private fun confirmDropoffIntent(): Observable<ConfirmDropoffIntent> {
        rideAddressView.confirmDropoffClicks()
            .compose(userEventLimiter())
            .map(::ConfirmDropoffIntent)
            .subscribe(confirmDropoffIntentPublisher)
        return confirmDropoffIntentPublisher
    }

    private fun confirmPickupIntent(): Observable<ConfirmPickupIntent> {
        rideAddressView.confirmPickupClicks()
            .compose(userEventLimiter())
            .map { ConfirmPickupIntent(it.first, it.second) }
            .subscribe(confirmPickupIntentPublisher)
        return confirmPickupIntentPublisher
    }

    private fun productTryAgainIntent(): Observable<ProductTryAgainIntent> {
        rideProductView.tryAgainClicks()
            .compose(userEventLimiter())
            .map { ProductTryAgainIntent(rideAddressView.pickupAdress, rideAddressView.dropoffAdress) }
            .subscribe(productTryAgainIntentPublisher)
        return productTryAgainIntentPublisher
    }

    private fun renderDropoff(newAddress: Address) {
        if (rideAddressView.dropoffAdress != newAddress.some()) {
            updateMapState(newAddress.location.latitude, newAddress.location.longitude, RideMarker.DROPOFF)
            rideAddressView.dropoffAdress = newAddress.some()
        }
    }

    private fun renderPickup(newAddress: Address) {
        if (rideAddressView.pickupAdress != newAddress.some()) {
            updateMapState(newAddress.location.latitude, newAddress.location.longitude, RideMarker.PICKUP)
            rideAddressView.pickupAdress = newAddress.some()
        }
    }

    private fun updateMapState(lat: Double, lng: Double, rideMarker: RideMarker) {
        mapFragment?.run {
            moveMapToLocation(lat, lng)
            addMarker(lat, lng, rideMarker)
        }
    }

    private fun showProductsView() {
        subscribeToProductHeightChanges()
        moveAddressCardViewUp()
        rideProductView.visibility = View.VISIBLE
    }

    private fun moveAddressCardViewUp() {
        TransitionManager.beginDelayedTransition(root, AutoTransition().addTarget(rideAddressView).addTarget(rideProductView))
        rideAddressView.run {
            hideActions()
            disableAddresses()
        }
        ConstraintSet().apply {
            clone(root)
            clear(rideAddressView.id, ConstraintSet.BOTTOM)
            connect(rideAddressView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            applyTo(root)
        }
    }

    private fun subscribeToAddressHeightChanges() {
        val subscription = rideAddressView.heightChanges()
            .doOnNext { mapFragment?.setMapPadding(it) }
            .retry()
            .subscribe()

        rideAddressViewSubscription = subscription
        subscription.disposeOnDestroy()
    }

    private fun subscribeToProductHeightChanges() {
        rideAddressViewSubscription.dispose()
        rideProductView.heightChanges()
            .doOnNext { mapFragment?.setMapPadding(it) }
            .retry()
            .subscribe()
            .disposeOnDestroy()
    }

    private fun showMessage(@StringRes resource: Int) {
        snackbar = Snackbar.make(root, resource, Snackbar.LENGTH_INDEFINITE).apply { show() }
    }

    private fun hideMessage() {
        snackbar?.dismiss()
    }

    private class BoundLocationCallback(
        private val locationPublisher: PublishSubject<NewLocationIntent>
    ) : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.forEach {
                locationPublisher.onNext(NewLocationIntent(it.some()))
            }
        }
    }

}
