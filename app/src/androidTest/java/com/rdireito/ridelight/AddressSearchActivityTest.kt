package com.rdireito.ridelight

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import com.rdireito.ridelight.Fabricator.address
import com.rdireito.ridelight.Fabricator.otherAddress
import com.rdireito.ridelight.common.RecyclerViewMatchers
import com.rdireito.ridelight.executor.AndroidTestSchedulerComposer
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivity
import io.reactivex.Maybe
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class AddressSearchActivityTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(AddressSearchActivity::class.java, true, false)

    private val app by lazy { InstrumentationRegistry.getTargetContext().applicationContext as TestApp }

    @Test
    fun typeAddress_itemsDisplayed() {
        whenever(app.dataSourceHolder.addressDataSource.addresses(any())).doReturn(
            Maybe.just(listOf(address(), otherAddress()))
        )

        rule.launchActivity(null)

        onView(withId(R.id.addressSearchAddressEdit))
            .perform(typeText("query"))

        waitDebounce()

        onView(
            RecyclerViewMatchers.withRecyclerView(R.id.addressSearchPlacesList)
                .atPosition(0)
                .onView(R.id.itemAddressSearchName)
        ).check(matches(withText(address().name)))

        onView(
            RecyclerViewMatchers.withRecyclerView(R.id.addressSearchPlacesList)
                .atPosition(1)
                .onView(R.id.itemAddressSearchName)
        ).check(matches(withText(otherAddress().name)))
    }

    @Test
    fun typeAddress_withOneLenght_clearButtonDisplayed() {
        rule.launchActivity(null)

        onView(withId(R.id.addressSearchAddressEdit))
            .perform(typeText("q"))
        onView(withId(R.id.addressSearchClearImage))
            .check(matches(isDisplayed()))
    }

    @Test
    fun typeAddress_withZeroLenght_clearButtonHidden() {
        rule.launchActivity(null)

        onView(withId(R.id.addressSearchAddressEdit))
            .perform(typeText(""))
        onView(withId(R.id.addressSearchClearImage))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun typeAddress_withError_errorDisplayed() {
        whenever(app.dataSourceHolder.addressDataSource.addresses(any())).doReturn(
            Maybe.error(Throwable("Error"))
        )

        rule.launchActivity(null)

        onView(withId(R.id.addressSearchAddressEdit))
            .perform(typeText("query"))

        waitDebounce()

        onView(withId(R.id.addressSearchErrorText))
            .check(matches(isDisplayed()))
    }

    private fun waitDebounce() {
        AndroidTestSchedulerComposer.computationScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS)
    }

}
