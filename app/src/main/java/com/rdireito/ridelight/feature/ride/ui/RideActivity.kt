package com.rdireito.ridelight.feature.ride.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.transition.*
import android.view.View
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jakewharton.rxbinding2.view.clicks
import com.rdireito.ridelight.R
import com.rdireito.ridelight.common.architecture.BaseView
import com.rdireito.ridelight.common.extension.*
import com.rdireito.ridelight.common.ui.ActivityResult
import com.rdireito.ridelight.common.ui.BaseActivity
import com.rdireito.ridelight.data.model.Address
import com.rdireito.ridelight.data.model.Location
import com.rdireito.ridelight.databinding.ActivityRideBinding
import com.rdireito.ridelight.feature.TAP_THROTTLE_TIME
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivity
import com.rdireito.ridelight.feature.ride.ui.RideViewModel.Companion.DROPOFF_REQUEST_CODE
import com.rdireito.ridelight.feature.ride.ui.RideViewModel.Companion.PICKUP_REQUEST_CODE
import com.rdireito.ridelight.feature.ride.ui.map.RideMapFragment
import com.rdireito.ridelight.feature.ride.ui.map.RideMapFragment.*
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiIntent
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiIntent.*
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiState
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_ride.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
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

    private val rxPermissions: RxPermissions by lazy { RxPermissions(this) }
    private var mapFragment: RideMapFragment? = null
        get() = findFragmentWithType(RideMapFragment.TAG)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: android.location.Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityRideBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_ride)

        init(savedInstanceState)
        bind()
        requestLocationPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(this, { location ->
                    if (location != null) {
                        this.location = location
                    }
                })
        }
    }

    private fun requestLocationPermission() {
        rxPermissions
            .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { permission ->
                if (permission.granted) {
                    // `permission.name` is granted !
                    Timber.d("permissionResult:granted=[${permission.granted}]")
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // Denied permission without ask never again
                    Timber.d("permissionResult:shouldShowRequestPermissionRationale=[${permission.shouldShowRequestPermissionRationale}]")
                    AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage(R.string.location_permission_rationale)
                        .setPositiveButton(R.string.ok) { _, _ -> requestLocationPermission() }
                        .show()
                } else {
                    // Denied permission with ask never again
                    // Need to go to the settings
                    Timber.d("permissionResult:neverAskAgain=[${permission}]")
                    AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage(R.string.location_permission_never_ask_again)
                        .setPositiveButton(R.string.ok) { _, _ -> goToAppSettings() }
                        .setNegativeButton(R.string.no) { _, _ -> finish() }
                        .show()
                }
            }
            .disposeOnDestroy()
    }

    private fun goToAppSettings() {
        openAppSettingsDetails()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val resultData = (data?.extras ?: Bundle.EMPTY).toMap()
        if (resultCode == Activity.RESULT_OK) {
            onActivityResultPublisher.onNext(OnActivityResultIntent(
                ActivityResult.SuccessWithData(requestCode, resultData)
            ))
        } else {
            onActivityResultPublisher.onNext(OnActivityResultIntent(
                ActivityResult.FailureWithData(requestCode, resultData)
            ))
        }
    }

    override fun intents(): Observable<RideUiIntent> =
        Observable.merge(listOf(
            initialIntent(),
            changeDropoffIntent(),
            changePickupIntent(),
            onActivityResultIntent(),
            confirmDropoffIntent(),
            confirmPickupIntent()
        ))

    override fun render(state: RideUiState) {
        if (state.invokeChangeDropoff) {
            startActivityForResult(AddressSearchActivity.getIntent(
                this, state.dropoffAdress.orNull()), DROPOFF_REQUEST_CODE
            )
            return
        }

        if (state.invokeChangePickup) {
            startActivityForResult(AddressSearchActivity.getIntent(
                this, state.pickupAdress.orNull()), PICKUP_REQUEST_CODE
            )
            return
        }

        state.dropoffAdress.map(this::renderDropoff)
        state.pickupAdress.map(this::renderPickup)

//        loadingProgress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
//        errorText.visibility = if (state.error != null) View.VISIBLE else View.GONE
//        state.error?.let {
//            errorText.text = "Error: ${it.localizedMessage}"
//            return
//        }

        state
            .estimates
            .takeIf { it.isNotEmpty() }
            ?.map {
                Timber.d("vehicle=[${it.vehicleType.name}]")
            }
    }

    private fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState == null)
            attachViews()
    }

    private fun attachViews() {
        supportFragmentManager.commitTransactions {
            it.attachFragment(RideMapFragment.newInstance(), R.id.rideMapContainer, RideMapFragment.TAG)
        }
    }

    private fun bind() {
        viewModel
            .states()
            .subscribe(this::render)
            .disposeOnDestroy()
        viewModel.processIntents(intents())
    }

    private fun initialIntent(): Observable<InitialIntent> =
        Observable.just(InitialIntent)

    private fun changeDropoffIntent(): Observable<ChangeDropoffIntent> {
        rideDropoffText.clicks()
            .compose(userEventLimiter())
            .map { ChangeDropoffIntent }
            .subscribe(changeDropoffIntentPublisher)
        return changeDropoffIntentPublisher
    }

    private fun changePickupIntent(): Observable<ChangePickupIntent> {
        ridePickupText.clicks()
            .compose(userEventLimiter())
            .map { ChangePickupIntent }
            .subscribe(changePickupIntentPublisher)
        return changePickupIntentPublisher
    }

    private fun onActivityResultIntent(): Observable<OnActivityResultIntent> =
        onActivityResultPublisher

    private fun confirmDropoffIntent(): Observable<ConfirmDropoffIntent> {
        rideConfirmDropoffButton.clicks()
            .compose(userEventLimiter())
//            .map { MainUiIntent.ConfirmDropoffLoadIntent(Address.ABSENT) }
            .doOnNext {
                showPickupFields()
            }
            .map { ConfirmDropoffIntent(Address("", "", "", "", "", Location(0.0, 0.0))) }
            .subscribe(confirmDropoffIntentPublisher)
        return confirmDropoffIntentPublisher
    }

    private fun showPickupFields() {
        TransitionManager.beginDelayedTransition(root)
        rideConfirmDropoffButton.visibility = View.GONE
        rideConfirmPickupButton.visibility = View.VISIBLE

        ConstraintSet().apply {
            clone(root)
            connect(ridePickupText.id, ConstraintSet.BOTTOM, rideDropoffText.id, ConstraintSet.TOP)
            applyTo(root)
        }
    }

    private fun confirmPickupIntent(): Observable<ConfirmPickupIntent> {
        rideConfirmPickupButton.clicks()
            .compose(userEventLimiter())
            .map { ConfirmPickupIntent(Address("", "", "", "", "", Location(0.0, 0.0))) }
            .subscribe(confirmPickupIntentPublisher)
        return confirmPickupIntentPublisher
    }

    private fun renderDropoff(newAddress: Address) {
        renderLocationChange(newAddress, rideDropoffText, RideMarker.DROPOFF)
    }

    private fun renderPickup(newAddress: Address) {
        renderLocationChange(newAddress, ridePickupText, RideMarker.PICKUP)
    }

    private fun renderLocationChange(newAddress: Address, addressTextView: TextView, rideMarker: RideMarker) {
        if (addressTextView.text.toString() != newAddress.address) {
            updateMapState(newAddress.location.latitude, newAddress.location.longitude, rideMarker)
            hideView(addressTextView) {
                addressTextView.text = newAddress.address
                showView(addressTextView)
            }
        }
    }

    private fun hideView(view: View, endAction: () -> Unit) {
        view.animate().apply {
            alpha(0f)
            interpolator = FastOutLinearInInterpolator()
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            withEndAction(endAction)
            start()
        }
    }

    private fun showView(view: View) {
        view.animate().apply {
            alpha(1f)
            interpolator = FastOutLinearInInterpolator()
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            start()
        }
    }

    private fun updateMapState(lat: Double, lng: Double, rideMarker: RideMarker) {
        mapFragment?.run {
            moveMapToLocation(lat, lng)
            addMarker(lat, lng, rideMarker)
        }
    }

    private fun <T> userEventLimiter(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.throttleFirst(TAP_THROTTLE_TIME, TimeUnit.MILLISECONDS)
        }
    }

}
