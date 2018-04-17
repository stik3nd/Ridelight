package com.rdireito.ridelight.data

import com.nhaarman.mockito_kotlin.*
import com.rdireito.ridelight.Fabricator.address
import com.rdireito.ridelight.Fabricator.otherAddress
import com.rdireito.ridelight.data.repository.AddressRepositoryImpl
import com.rdireito.ridelight.data.source.AddressDataSource
import io.reactivex.Maybe
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddressRepositoryTest {

    private val addressDataSource: AddressDataSource = mock()
    @InjectMocks
    private lateinit var repository: AddressRepositoryImpl

    @Test
    fun addresses_withQuery_shouldReturnAddresses() {
        val query = "my street"
        val addresses = listOf(address(), otherAddress())

        whenever(addressDataSource.addresses(any())).doReturn(Maybe.just(addresses))

        val testObserver = repository.addresses(query).test()
        verify(addressDataSource).addresses(eq(query))
        testObserver.assertNoErrors()
        testObserver.assertValue(addresses)
        testObserver.assertComplete()
    }

    @Test
    fun addresses_withError_shouldReturnError() {
        val query = "my street"
        val error = Throwable("Error")

        whenever(addressDataSource.addresses(any())).doReturn(Maybe.error(error))

        val testObserver = repository.addresses(query).test()
        verify(addressDataSource).addresses(eq(query))
        testObserver.assertError(error)
        testObserver.assertTerminated()
    }

}
