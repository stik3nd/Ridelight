package com.rdireito.ridelight.addresssearch

import arrow.syntax.option.some
import com.nhaarman.mockito_kotlin.*
import com.rdireito.ridelight.Fabricator.address
import com.rdireito.ridelight.Fabricator.otherAddress
import com.rdireito.ridelight.data.repository.AddressRepository
import com.rdireito.ridelight.executor.TestSchedulerComposer
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchUiIntent
import com.rdireito.ridelight.feature.addresssearch.mvi.AddressSearchUiState
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchActionProcessor
import com.rdireito.ridelight.feature.addresssearch.ui.AddressSearchViewModel
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddressSearchViewModelTest {

    private val addressRepository: AddressRepository = mock()
    private val scheduler: TestSchedulerComposer = TestSchedulerComposer()

    private lateinit var actionProcessor: AddressSearchActionProcessor
    private lateinit var viewModel: AddressSearchViewModel
    private lateinit var testObserver: TestObserver<AddressSearchUiState>

    @Before
    fun setup() {
        actionProcessor = AddressSearchActionProcessor(addressRepository, scheduler)
        viewModel = AddressSearchViewModel(actionProcessor)
        testObserver = viewModel.states().test()
    }


    @Test
    fun clearAddress_shouldClearAddress() {
        viewModel.processIntents(Observable.just(AddressSearchUiIntent.ClearAddressIntent))

        testObserver.assertValueAt(1) { state ->
            state == AddressSearchUiState.idle().copy(clearQuery = true)
        }
        assertThat(testObserver.valueCount(), `is`(2))
    }

    @Test
    fun inputSearchText_withZeroLenght_shouldHideClearButton() {
        val length = 0

        viewModel.processIntents(Observable.just(
            AddressSearchUiIntent.InputSearchTextIntent(length)))

        testObserver.assertValue { state ->
            state.clearQuery.not() && state.hasClearButton.not() && state.addresses.isEmpty()
        }
        assertThat(testObserver.valueCount(), `is`(1))
    }

    @Test
    fun inputSearchText_withOneLenght_shouldShowClearButton() {
        val length = 1

        viewModel.processIntents(Observable.just(
            AddressSearchUiIntent.InputSearchTextIntent(length)))

        testObserver.assertValueAt(1) { state ->
            state.clearQuery.not() && state.hasClearButton
        }
        assertThat(testObserver.valueCount(), `is`(2))
    }

    @Test
    fun changeAddress_withQuery_shouldShowLoadindAndAddresses() {
        // given
        val query = "my street"
        val addressesResult = listOf(address(), otherAddress())
        whenever(addressRepository.addresses(query)).doReturn(Maybe.just(addressesResult))

        // when
        viewModel.processIntents(Observable.just(
            AddressSearchUiIntent.ChangeAddressIntent(query)))

        // then
        verify(addressRepository).addresses(eq(query))
        testObserver.assertValueAt(1) { (isLoading, clearQuery, _, error, _, _) ->
            isLoading && error == null && clearQuery.not()
        }
        testObserver.assertValueAt(2) { (isLoading, clearQuery, addresses, error, _, _) ->
            isLoading.not() && addresses == addressesResult && error == null && clearQuery.not()
        }
        assertThat(testObserver.valueCount(), `is`(3))
    }

    @Test
    fun changeAddress_withQueryError_shouldShowLoadindAndError() {
        // given
        val query = "my street"
        val errorResult = Throwable("Error")
        whenever(addressRepository.addresses(query)).doReturn(Maybe.error(errorResult))

        // when
        viewModel.processIntents(Observable.just(
            AddressSearchUiIntent.ChangeAddressIntent(query)))

        // then
        verify(addressRepository).addresses(eq(query))
        testObserver.assertValueAt(1) { (isLoading, clearQuery, _, error, _, _) ->
            isLoading && error == null && clearQuery.not()
        }
        testObserver.assertValueAt(2) { (isLoading, clearQuery, addresses, error, _, _) ->
            isLoading.not() && addresses.isEmpty() && error == errorResult && clearQuery.not()
        }
        assertThat(testObserver.valueCount(), `is`(3))
    }

    @Test
    fun selectAddress_shouldSetSelected() {
        val selected = address()

        viewModel.processIntents(Observable.just(
            AddressSearchUiIntent.SelectAddressIntent(selected)))

        testObserver.assertValueAt(1) { state ->
            state.selectedAddress == selected.some()
        }
        assertThat(testObserver.valueCount(), `is`(2))
    }
}
