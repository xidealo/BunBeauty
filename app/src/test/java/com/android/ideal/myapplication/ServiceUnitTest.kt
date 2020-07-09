package com.android.ideal.myapplication

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.PhotoRepository
import io.mockk.mockk
import org.mockito.Mock

open class ServiceUnitTest {

    @Mock
    val intent: Intent = mockk()
    @Mock
    val photoRepository: PhotoRepository = mockk()

  /*  @Test
    fun whenServiceIsPremium() {
        val dateString = WorkWithTimeApi.getDateInFormatYMDHMS(Date(Date().time + 1000 * 60 * 60))
        assertTrue(getServiceInstance().isPremium(dateString))
    }

    @Test
    fun whenServiceIsNotPremium() {
        val dateString = WorkWithTimeApi.getDateInFormatYMDHMS(Date(Date().time - 1000 * 60 * 60))
        assertFalse(getServiceInstance().isPremium(dateString))
    }*/

    private fun getServiceInstance() =  ServiceServiceInteractor(intent)
}