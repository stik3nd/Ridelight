package com.rdireito.ridelight.data

import com.nhaarman.mockito_kotlin.*
import com.rdireito.ridelight.Fabricator.address
import com.rdireito.ridelight.Fabricator.estimate1
import com.rdireito.ridelight.Fabricator.estimate2
import com.rdireito.ridelight.Fabricator.otherAddress
import com.rdireito.ridelight.data.model.request.EstimateRequest
import com.rdireito.ridelight.data.network.api.EstimateApi
import com.rdireito.ridelight.data.repository.EstimateRepositoryImpl
import io.reactivex.Single
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class EstimatesRepositoryTest {

    private val estimateApi: EstimateApi = mock()
    @InjectMocks
    private lateinit var repository: EstimateRepositoryImpl

    @Test
    fun estimates_withRequest_shouldReturnEstimates() {
        val estimateRequest = EstimateRequest(address(), otherAddress())
        val estimatesResult = listOf(estimate1(), estimate2())

        whenever(estimateApi.estimates(any())).doReturn(Single.just(estimatesResult))

        val testObserver = repository.estimates(estimateRequest).test()
        verify(estimateApi).estimates(eq(estimateRequest))
        testObserver.assertNoErrors()
        testObserver.assertValue(estimatesResult)
        testObserver.assertComplete()
    }

    @Test
    fun estimates_withError_shouldReturnError() {
        val estimateRequest = EstimateRequest(address(), otherAddress())
        val error = Throwable("Error")
        whenever(estimateApi.estimates(any())).doReturn(Single.error(error))

        val testObserver = repository.estimates(estimateRequest).test()
        verify(estimateApi).estimates(eq(estimateRequest))
        testObserver.assertError(error)
        testObserver.assertTerminated()
    }

}
