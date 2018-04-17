package com.rdireito.ridelight.ride

import arrow.core.Option
import arrow.syntax.either.left
import arrow.syntax.either.right
import arrow.syntax.option.none
import arrow.syntax.option.some
import com.nhaarman.mockito_kotlin.*
import com.rdireito.ridelight.Fabricator.address
import com.rdireito.ridelight.Fabricator.otherAddress
import com.rdireito.ridelight.Fabricator.location
import com.rdireito.ridelight.Fabricator.estimate1
import com.rdireito.ridelight.Fabricator.estimate2
import com.rdireito.ridelight.common.ui.ActivityResult
import com.rdireito.ridelight.data.model.*
import com.rdireito.ridelight.data.model.request.EstimateRequest
import com.rdireito.ridelight.data.repository.AddressRepository
import com.rdireito.ridelight.data.repository.EstimateRepository
import com.rdireito.ridelight.executor.TestSchedulerComposer
import com.rdireito.ridelight.feature.MESSAGE_TIME
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActivity.Companion.EXTRA_ADDRESS
import com.rdireito.ridelight.feature.ride.ui.RideActionProcessor
import com.rdireito.ridelight.feature.ride.ui.RideViewModel
import com.rdireito.ridelight.feature.ride.ui.RideViewModel.Companion.DROPOFF_REQUEST_CODE
import com.rdireito.ridelight.feature.ride.ui.RideViewModel.Companion.PICKUP_REQUEST_CODE
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiIntent
import com.rdireito.ridelight.feature.ride.ui.mvi.RideUiState
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class RideViewModelTest {

    private val estimateRepository: EstimateRepository = mock()
    private val addressRepository: AddressRepository = mock()
    private val scheduler: TestSchedulerComposer = TestSchedulerComposer()

    private lateinit var actionProcessor: RideActionProcessor
    private lateinit var viewModel: RideViewModel
    private lateinit var testObserver: TestObserver<RideUiState>

    @Before
    fun setup() {
        actionProcessor = RideActionProcessor(estimateRepository, addressRepository, scheduler)
        viewModel = RideViewModel(actionProcessor)
        testObserver = viewModel.states().test()
    }

    @Test
    fun init_withoutInitialValue_shouldShowIdle() {
        // given initial state null
        val initialUiState: RideUiState? = null

        //when process intents (first call)
        viewModel.processIntents(Observable.just(RideUiIntent.InitialIntent(initialUiState)))

        // then ui is idle
        testObserver.assertValue(RideUiState.idle())
        assertThat(testObserver.valueCount(), `is`(1))
    }

    @Test
    fun init_withInitialValue_shouldShowInitialValue() {
        // given initial value different from idle
        val initialUiState: RideUiState = RideUiState.idle().copy(pickupAddress = address())

        //when process intents
        viewModel.processIntents(Observable.just(RideUiIntent.InitialIntent(initialUiState)))

        // then ui is idle
        assertThat(testObserver.valueCount(), `is`(2))
        testObserver.assertValueAt(1) { state ->
            state == initialUiState
        }
    }

    @Test
    fun activityResult_withDropoff_shouldShowDropoff() {
        val dropoffAddress = address()
        val eitherActivityResult = ActivityResult.SuccessWithData(
            DROPOFF_REQUEST_CODE, mapOf(EXTRA_ADDRESS to dropoffAddress)
        ).right()

        viewModel.processIntents(Observable.just(
            RideUiIntent.OnActivityResultIntent(eitherActivityResult)))

        testObserver.assertValueAt(1) { state ->
            state.dropoffAddress == dropoffAddress
        }
    }

    @Test
    fun activityResult_withPickup_shouldShowPickup() {
        val pickupAddress = address()
        val eitherActivityResult = ActivityResult.SuccessWithData(
            PICKUP_REQUEST_CODE, mapOf(EXTRA_ADDRESS to pickupAddress)
        ).right()

        viewModel.processIntents(Observable.just(
            RideUiIntent.OnActivityResultIntent(eitherActivityResult)))

        testObserver.assertValueAt(1) { state ->
            state.pickupAddress == pickupAddress
        }
    }

    @Test
    fun activityResult_withoutValue_shouldShowPreviousState() {
        val eitherActivityResult = ActivityResult.FailureWithData(-1, mapOf()).left()

        viewModel.processIntents(Observable.just(
            RideUiIntent.OnActivityResultIntent(eitherActivityResult)))

        testObserver.assertValue { state ->
            state.dropoffAddress == null && state.pickupAddress == null
        }
        assertThat(testObserver.valueCount(), `is`(1))
    }

    @Test
    fun confirmDropoff_withoutAddress_showHideMessage() {
        val dropoffAddress: Option<Address> = none()

        viewModel.processIntents(Observable.just(
            RideUiIntent.ConfirmDropoffIntent(dropoffAddress)))

        verify(addressRepository, never()).addressByLocation(any(), any())
        // show message
        testObserver.assertValueAt(1) { state ->
            state.showPickupFields.not() && state.invalidAddress
        }
        assertThat(testObserver.valueCount(), `is`(2))

        scheduler.computationScheduler.advanceTimeBy(MESSAGE_TIME, TimeUnit.MILLISECONDS)

        // hide message
        testObserver.assertValueAt(2) { state ->
            state.invalidAddress.not()
        }
        assertThat(testObserver.valueCount(), `is`(3))
    }

    @Test
    fun confirmDropoff_withAddressWithoutPosition_showPickupFields() {
        val dropoffAddress: Option<Address> = address().some()
        val currentPosition: Option<Location> = none()

        viewModel.currentPosition = currentPosition
        viewModel.processIntents(Observable.just(
            RideUiIntent.ConfirmDropoffIntent(dropoffAddress)))

        verify(addressRepository, never()).addressByLocation(any(), any())
        testObserver.assertValueAt(1) { state ->
            state.showPickupFields && state.invalidAddress.not()
        }
    }

    @Test
    fun confirmDropoff_withAddressAndPosition_showPickupFieldsFilled() {
        // given
        val dropoffAddress: Option<Address> = address().some()
        val currentPositionUnfold = location()
        val currentPosition: Option<Location> = currentPositionUnfold.some()
        val pickupAddress: Address = otherAddress()
        whenever(addressRepository.addressByLocation(any(), any())).doReturn(Maybe.just(pickupAddress))

        // when
        viewModel.currentPosition = currentPosition
        viewModel.processIntents(Observable.just(
            RideUiIntent.ConfirmDropoffIntent(dropoffAddress)))

        // then
        verify(addressRepository).addressByLocation(eq(currentPositionUnfold.latitude), eq(currentPositionUnfold.longitude))
        testObserver.assertValueAt(1) { state ->
            state.showPickupFields && state.invalidAddress.not()
        }
        testObserver.assertValueAt(2) { state ->
            state.pickupAddress == pickupAddress
        }
    }

    @Test
    fun confirmDropoff_withAddressAndPositionError_showPickupFields() {
        // given
        val dropoffAddress: Option<Address> = address().some()
        val currentPositionUnfold = location()
        val currentPosition: Option<Location> = currentPositionUnfold.some()
        whenever(addressRepository.addressByLocation(any(), any())).doReturn(Maybe.empty<Address>())

        // when
        viewModel.currentPosition = currentPosition
        viewModel.processIntents(Observable.just(
            RideUiIntent.ConfirmDropoffIntent(dropoffAddress)))

        // then
        verify(addressRepository).addressByLocation(eq(currentPositionUnfold.latitude), eq(currentPositionUnfold.longitude))
        testObserver.assertValueAt(1) { state ->
            state.showPickupFields && state.invalidAddress.not() && state.pickupAddress == null
        }
    }

    @Test
    fun confirmPickup_withoutAddresses_showHideMessage() {
        val pickupAddress: Option<Address> = none()
        val dropoffAddress: Option<Address> = none()

        viewModel.processIntents(Observable.just(
            RideUiIntent.ConfirmPickupIntent(pickupAddress, dropoffAddress)))

        verify(estimateRepository, never()).estimates(any())
        // show message
        testObserver.assertValueAt(1) { state ->
            state.invalidAddress
        }
        scheduler.computationScheduler.advanceTimeBy(MESSAGE_TIME, TimeUnit.MILLISECONDS)
        // hide message
        testObserver.assertValueAt(2) { state ->
            state.invalidAddress.not()
        }
    }

    @Test
    fun confirmPickup_withoutPickup_showHideMessage() {
        val pickupAddress: Option<Address> = none()
        val dropoffAddress: Option<Address> = address().some()

        viewModel.processIntents(Observable.just(
            RideUiIntent.ConfirmPickupIntent(pickupAddress, dropoffAddress)))

        verify(estimateRepository, never()).estimates(any())
        // show message
        testObserver.assertValueAt(1) { state ->
            state.invalidAddress
        }
        scheduler.computationScheduler.advanceTimeBy(MESSAGE_TIME, TimeUnit.MILLISECONDS)
        // hide message
        testObserver.assertValueAt(2) { state ->
            state.invalidAddress.not()
        }
    }

    @Test
    fun confirmPickup_withoutDropoff_showHideMessage() {
        val pickupAddress: Option<Address> = address().some()
        val dropoffAddress: Option<Address> = none()

        viewModel.processIntents(Observable.just(
            RideUiIntent.ConfirmPickupIntent(pickupAddress, dropoffAddress)))

        verify(estimateRepository, never()).estimates(any())
        // show message
        testObserver.assertValueAt(1) { state ->
            state.invalidAddress
        }
        scheduler.computationScheduler.advanceTimeBy(MESSAGE_TIME, TimeUnit.MILLISECONDS)
        // hide message
        testObserver.assertValueAt(2) { state ->
            state.invalidAddress.not()
        }
    }

    @Test
    fun confirmPickup_withAddresses_showLoadingAndProducts() {
        // given
        val pickupAddress: Option<Address> = address().some()
        val dropoffAddress: Option<Address> = otherAddress().some()
        val estimateRequest = EstimateRequest(address(), otherAddress())
        val estimatesResult = listOf(estimate1(), estimate2())
        whenever(estimateRepository.estimates(any())).doReturn(Single.just(estimatesResult))

        // when
        viewModel.processIntents(Observable.just(
            RideUiIntent.ConfirmPickupIntent(pickupAddress, dropoffAddress)))

        // then
        verify(estimateRepository).estimates(eq(estimateRequest))
        testObserver.assertValueAt(1) { (isLoading, estimates, error, _, _, _, showProducts, _) ->
            isLoading && showProducts && error == null && estimates.isEmpty()
        }
        testObserver.assertValueAt(2) { (isLoading, estimates, error, _, _, _, _, _) ->
            isLoading.not() && error == null && estimates == estimatesResult
        }
        assertThat(testObserver.valueCount(), `is`(3))
    }

    @Test
    fun confirmPickup_withError_showLoadingAndError() {
        // given
        val pickupAddress: Option<Address> = address().some()
        val dropoffAddress: Option<Address> = otherAddress().some()
        val estimateRequest = EstimateRequest(address(), otherAddress())
        val errorResult = Throwable("Error")
        whenever(estimateRepository.estimates(any())).doReturn(Single.error(errorResult))

        // when
        viewModel.processIntents(Observable.just(
            RideUiIntent.ConfirmPickupIntent(pickupAddress, dropoffAddress)))

        // then
        verify(estimateRepository).estimates(eq(estimateRequest))
        testObserver.assertValueAt(1) { (isLoading, estimates, error, _, _, _, showProducts, _) ->
            isLoading && showProducts && error == null && estimates.isEmpty()
        }
        testObserver.assertValueAt(2) { (isLoading, estimates, error, _, _, _, _, _) ->
            isLoading.not() && error == errorResult && estimates.isEmpty()
        }
        assertThat(testObserver.valueCount(), `is`(3))
    }

    @Test
    fun tryAgain_withoutAddresses_showHideMessage() {
        val pickupAddress: Option<Address> = none()
        val dropoffAddress: Option<Address> = none()

        viewModel.processIntents(Observable.just(
            RideUiIntent.ProductTryAgainIntent(pickupAddress, dropoffAddress)))

        verify(estimateRepository, never()).estimates(any())
        // show message
        testObserver.assertValueAt(1) { state ->
            state.invalidAddress
        }
        scheduler.computationScheduler.advanceTimeBy(MESSAGE_TIME, TimeUnit.MILLISECONDS)
        // hide message
        testObserver.assertValueAt(2) { state ->
            state.invalidAddress.not()
        }
    }

    @Test
    fun tryAgain_withoutPickup_showHideMessage() {
        val pickupAddress: Option<Address> = none()
        val dropoffAddress: Option<Address> = address().some()

        viewModel.processIntents(Observable.just(
            RideUiIntent.ProductTryAgainIntent(pickupAddress, dropoffAddress)))

        verify(estimateRepository, never()).estimates(any())
        // show message
        testObserver.assertValueAt(1) { state ->
            state.invalidAddress
        }
        scheduler.computationScheduler.advanceTimeBy(MESSAGE_TIME, TimeUnit.MILLISECONDS)
        // hide message
        testObserver.assertValueAt(2) { state ->
            state.invalidAddress.not()
        }
    }

    @Test
    fun tryAgain_withoutDropoff_showHideMessage() {
        val pickupAddress: Option<Address> = address().some()
        val dropoffAddress: Option<Address> = none()

        viewModel.processIntents(Observable.just(
            RideUiIntent.ProductTryAgainIntent(pickupAddress, dropoffAddress)))

        verify(estimateRepository, never()).estimates(any())
        // show message
        testObserver.assertValueAt(1) { state ->
            state.invalidAddress
        }
        scheduler.computationScheduler.advanceTimeBy(MESSAGE_TIME, TimeUnit.MILLISECONDS)
        // hide message
        testObserver.assertValueAt(2) { state ->
            state.invalidAddress.not()
        }
    }

    @Test
    fun tryAgain_withAddresses_showLoadingAndProducts() {
        // given
        val pickupAddress: Option<Address> = address().some()
        val dropoffAddress: Option<Address> = otherAddress().some()
        val estimateRequest = EstimateRequest(address(), otherAddress())
        val estimatesResult = listOf(estimate1(), estimate2())
        whenever(estimateRepository.estimates(any())).doReturn(Single.just(estimatesResult))

        // when
        viewModel.processIntents(Observable.just(
            RideUiIntent.ProductTryAgainIntent(pickupAddress, dropoffAddress)))

        // then
        verify(estimateRepository).estimates(eq(estimateRequest))
        testObserver.assertValueAt(1) { (isLoading, estimates, error, _, _, _, showProducts, _) ->
            isLoading && showProducts && error == null && estimates.isEmpty()
        }
        testObserver.assertValueAt(2) { (isLoading, estimates, error, _, _, _, _, _) ->
            isLoading.not() && error == null && estimates == estimatesResult
        }
        assertThat(testObserver.valueCount(), `is`(3))
    }

    @Test
    fun tryAgain_withError_showLoadingAndError() {
        // given
        val pickupAddress: Option<Address> = address().some()
        val dropoffAddress: Option<Address> = otherAddress().some()
        val estimateRequest = EstimateRequest(address(), otherAddress())
        val errorResult = Throwable("Error")
        whenever(estimateRepository.estimates(any())).doReturn(Single.error(errorResult))

        // when
        viewModel.processIntents(Observable.just(
            RideUiIntent.ProductTryAgainIntent(pickupAddress, dropoffAddress)))

        // then
        verify(estimateRepository).estimates(eq(estimateRequest))
        testObserver.assertValueAt(1) { (isLoading, estimates, error, _, _, _, showProducts, _) ->
            isLoading && showProducts && error == null && estimates.isEmpty()
        }
        testObserver.assertValueAt(2) { (isLoading, estimates, error, _, _, _, _, _) ->
            isLoading.not() && error == errorResult && estimates.isEmpty()
        }
        assertThat(testObserver.valueCount(), `is`(3))
    }

    @Test
    fun changeDropoff_noSideEffect() {
        viewModel.processIntents(Observable.just(RideUiIntent.ChangeDropoffIntent))

        testObserver.assertValueAt(0) { state ->
            state == RideUiState.idle()
        }
        assertThat(testObserver.valueCount(), `is`(1))
    }

    @Test
    fun changePickup_noSideEffect() {
        viewModel.processIntents(Observable.just(RideUiIntent.ChangePickupIntent))

        testObserver.assertValueAt(0) { state ->
            state == RideUiState.idle()
        }
        assertThat(testObserver.valueCount(), `is`(1))
    }

    @Test
    fun newLocationIntent_noSideEffect() {
        viewModel.processIntents(Observable.just(RideUiIntent.NewLocationIntent(location().some())))

        testObserver.assertValueAt(0) { state ->
            state == RideUiState.idle()
        }
        assertThat(testObserver.valueCount(), `is`(1))
    }

}
