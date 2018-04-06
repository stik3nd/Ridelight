package com.rdireito.ridelight.feature.ride.ui.widget

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import com.jakewharton.rxbinding2.view.clicks
import com.rdireito.ridelight.R
import com.rdireito.ridelight.feature.TAP_THROTTLE_TIME
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.view_ride_address_card.view.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class RideAddressCardView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attributeSet, defStyleAttr) {

  private val heightChanges = BehaviorSubject.create<Int>()

  var address by Delegates.observable<String?>(null) { _, _, newValue ->
    newValue?.let { showAddressContent(it) } ?: showAddressAbsent()
  }

  init {
    initLayout()
  }

  private fun initLayout() {
    inflate(context, R.layout.view_ride_address_card, this)
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)

    if (heightChanges.hasObservers() && heightChanges.hasComplete().not()) {
      heightChanges.onNext(h)
    }
  }

  fun heightChanges(): Observable<Int> =
      heightChanges.distinctUntilChanged()
          .doOnNext { Timber.d("RideAddressCardView height has changed=[$it]") }

  fun nextClicks(): Observable<Unit> =
      buttonNext.clicks()
          .throttleFirst(TAP_THROTTLE_TIME, TimeUnit.MILLISECONDS)
//          .observeOn(AndroidSchedulers.mainThread())

  fun addressClicks(): Observable<Unit> {
    val dropOffViewClicks = addAddressIcon.clicks()
    val radarClicks = textAddress.clicks()

    return dropOffViewClicks.mergeWith(radarClicks)
        .throttleFirst(TAP_THROTTLE_TIME, TimeUnit.MILLISECONDS)
//        .observeOn(AndroidSchedulers.mainThread())
  }

  fun showAddressContent(address: String) {
    textAddress.text = address
  }

  fun showAddressAbsent() {
    textAddress.text = context.getString(R.string.dropoff)
  }

//  fun showAddressLoading() {
//    textAddress.text = "Loading..."//context.getString(R.string.loading)
//  }

}
