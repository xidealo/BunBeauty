package com.android.ideal.myapplication

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.PhotoServiceRepository
import io.mockk.mockk
import org.mockito.Mock

open class ServiceUnitTest {

    @Mock
    val intent: Intent = mockk()
    @Mock
    val photoServiceRepository: PhotoServiceRepository = mockk()

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

    //private fun getServiceInstance() =  ServiceInteractor(intent)
}