package com.rdireito.ridelight.feature.ride.ui.widget

import android.content.Context
import android.support.constraint.ConstraintSet
import android.support.v7.widget.CardView
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import arrow.core.Option
import arrow.syntax.option.none
import com.jakewharton.rxbinding2.view.clicks
import com.rdireito.ridelight.R
import com.rdireito.ridelight.data.model.Address
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.view_ride_address_card.view.*
import timber.log.Timber
import kotlin.properties.Delegates

class RideAddressCardView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attributeSet, defStyleAttr) {

    private val animTime by lazy { resources.getInteger(android.R.integer.config_mediumAnimTime).toLong() }
    private val heightChanges = BehaviorSubject.create<Int>()


    var dropoffAddress: Option<Address> by Delegates.observable(none()) { _, _, newValue ->
        newValue.map { rideDropoffAddressView.updateAddress(it.address) }
    }
    var pickupAddress: Option<Address> by Delegates.observable(none()) { _, _, newValue ->
        newValue.map { ridePickupAddressView.updateAddress(it.address) }
    }

    init {
        inflate(context, R.layout.view_ride_address_card, this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (heightChanges.hasObservers() && heightChanges.hasComplete().not()) {
            heightChanges.onNext(h)
        }
    }

    fun dropoffClicks(): Observable<Unit> = rideDropoffAddressView.clicks()

    fun pickupClicks(): Observable<Unit> = ridePickupAddressView.clicks()

    fun confirmDropoffClicks(): Observable<Option<Address>> =
        dropoffActionContainer.clicks().map { dropoffAddress }

    fun confirmPickupClicks(): Observable<Pair<Option<Address>, Option<Address>>> =
        pickupActionContainer.clicks().map { Pair(pickupAddress, dropoffAddress) }

    fun showPickupFields() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(context, R.layout.view_ride_address_card_pickup)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = animTime

        TransitionManager.beginDelayedTransition(root, transition)
        constraintSet.applyTo(root)
    }

    fun hideActions() {
        dropoffActionContainer.visibility = View.GONE
        pickupActionContainer.visibility = View.GONE
    }

    fun disableAddresses() {
        rideDropoffAddressView.isEnabled = false
        ridePickupAddressView.isEnabled = false
    }

    fun heightChanges(): Observable<Int> =
        heightChanges.distinctUntilChanged()
            .doOnNext { Timber.d("RideAddressCardView height has changed=[$it]") }

}
