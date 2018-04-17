package com.rdireito.ridelight

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import arrow.syntax.option.some
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import com.rdireito.ridelight.Fabricator.address
import com.rdireito.ridelight.Fabricator.estimate1
import com.rdireito.ridelight.Fabricator.estimate2
import com.rdireito.ridelight.Fabricator.otherAddress
import com.rdireito.ridelight.common.RecyclerViewMatchers
import com.rdireito.ridelight.feature.ride.ui.RideActivity
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_ride.*
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RideActivityTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(RideActivity::class.java, true, false)

    private val app by lazy { InstrumentationRegistry.getTargetContext().applicationContext as TestApp }

    @Test
    fun setDropoff_dropoffDisplayed() {
        rule.launchActivity(null)

        onView(withId(R.id.rideAddressView))
            .check(matches(isDisplayed()))

        rule.runOnUiThread {
            rule.activity.rideAddressView.dropoffAddress = address().some()
        }

        onView(withText(address().address))
            .check(matches(isDisplayed()))

        onView(withId(R.id.dropoffActionContainer))
            .check(matches(isDisplayed()))
    }

    @Test
    fun confirmDropoff_withoutAddress_messageDisplayed() {
        rule.launchActivity(null)

        onView(withId(R.id.dropoffActionContainer))
            .perform(click())

        val message = InstrumentationRegistry.getTargetContext().getString(R.string.select_address)

        onView(withText(message))
            .check(matches(isDisplayed()))
    }


    @Test
    fun setDropoffAndPickup_dropoffPickupAndConfirmPickupDisplayed() {
        val dropoffAddress = address()
        val pickupAddress = otherAddress()
        whenever(app.dataSourceHolder.addressDataSource.addressByLocation(any(), any())).doReturn(
            Maybe.just(pickupAddress)
        )

        rule.launchActivity(null)

        // set dropoff
        rule.runOnUiThread {
            rule.activity.rideAddressView.dropoffAddress = dropoffAddress.some()
        }
        onView(withText(dropoffAddress.address))
            .check(matches(isDisplayed()))

        // confirm dropoff
        onView(withId(R.id.dropoffActionContainer))
            .perform(click())

        // appear pickup field after confirming dropoff
        onView(withId(R.id.ridePickupAddressView))
            .check(matches(isDisplayed()))
//
        // set pickup
        rule.runOnUiThread {
            rule.activity.rideAddressView.pickupAddress = pickupAddress.some()
        }
        onView(withText(pickupAddress.address))
            .check(matches(isDisplayed()))

        // appear pickup confirm
        onView(withId(R.id.dropoffActionContainer))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.pickupActionContainer))
            .check(matches(isDisplayed()))
    }

    @Test
    fun confirmPickup_productsDisplayed() {
        val dropoffAddress = address()
        val pickupAddress = otherAddress()
        val product1 = estimate1()
        val product2 = estimate2()
        whenever(app.dataSourceHolder.addressDataSource.addressByLocation(any(), any())).doReturn(
            Maybe.just(pickupAddress)
        )
        whenever(app.apiHolder.estimateApi.estimates(any())).doReturn(
            Single.just(listOf(product1, product2))
        )

        rule.launchActivity(null)

        // set/confirm dropoff
        rule.runOnUiThread {
            rule.activity.rideAddressView.dropoffAddress = dropoffAddress.some()
        }
        onView(withId(R.id.dropoffActionContainer))
            .perform(click())

        // set/confirm pickup
        rule.runOnUiThread {
            rule.activity.rideAddressView.pickupAddress = pickupAddress.some()
        }
        onView(withId(R.id.pickupActionContainer))
            .perform(click())

        // verify products displayed
        onView(withId(R.id.rideAddressView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rideProductView))
            .check(matches(isDisplayed()))

        onView(
            RecyclerViewMatchers.withRecyclerView(R.id.rideProductsList)
                .atPosition(0)
                .onView(R.id.productName)
        ).check(matches(withText(product1.vehicleType.name)))

        onView(
            RecyclerViewMatchers.withRecyclerView(R.id.rideProductsList)
                .atPosition(1)
                .onView(R.id.productName)
        ).check(matches(withText(product2.vehicleType.name)))

        Thread.sleep(5000)
    }

}
