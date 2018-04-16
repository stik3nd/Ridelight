package com.rdireito.ridelight.feature.ride.ui.widget

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.rdireito.ridelight.R
import com.rdireito.ridelight.data.model.Estimate
import com.rdireito.ridelight.feature.ride.ui.RideProductAdapter
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.view_ride_product_card.view.*
import timber.log.Timber

class RideProductView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attributeSet, defStyleAttr) {

    private val heightChanges = BehaviorSubject.create<Int>()
    private val productAdapter = RideProductAdapter { item, pos ->
        onProductClicked(item, pos)
    }

    private fun onProductClicked(item: Estimate, pos: Int) {
        Timber.d("Selected product=[$item]")
        productAdapter.setSelected(pos)
    }

    init {
        inflate(context, R.layout.view_ride_product_card, this)

        rideProductsList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = productAdapter
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (heightChanges.hasObservers() && heightChanges.hasComplete().not()) {
            heightChanges.onNext(h)
        }
    }

    fun setLoading(loading: Boolean) {
        rideProductsProgress.visibility = if (loading) View.VISIBLE else View.GONE
    }

    fun setError(error: Throwable?) {
        if (error != null) {
            Timber.e(error)
            rideProductsErrorText.visibility = View.VISIBLE
            rideProductsTryAgainButton.visibility = View.VISIBLE
            productAdapter.clear()
        } else {
            rideProductsErrorText.visibility = View.GONE
            rideProductsTryAgainButton.visibility = View.GONE
        }
    }

    fun setProducts(estimates: List<Estimate>) {
        if (estimates.isNotEmpty()) {
            productAdapter.clear()
            productAdapter.addAll(estimates)
        }
    }

    fun heightChanges(): Observable<Int> =
        heightChanges.distinctUntilChanged()
            .doOnNext { Timber.d("RideProductView height has changed=[$it]") }

    fun tryAgainClicks(): Observable<Unit> = rideProductsTryAgainButton.clicks()

}
