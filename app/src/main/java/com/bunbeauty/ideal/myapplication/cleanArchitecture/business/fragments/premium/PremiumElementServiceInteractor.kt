package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.CheckPremiumPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*

class PremiumElementServiceInteractor(val serviceRepository: ServiceRepository) :
    UpdateServiceCallback {

    lateinit var service: Service
    lateinit var checkPremiumPresenterCallback: CheckPremiumPresenterCallback

    fun activatePremium(
        service: Service,
        checkPremiumPresenterCallback: CheckPremiumPresenterCallback
    ) {

        this.checkPremiumPresenterCallback = checkPremiumPresenterCallback
        service.premiumDate = addSevenDayPremium(service)
        serviceRepository.update(service, this)
    }

    private fun addSevenDayPremium(service: Service): String {
        val sysdateLong: Long = if (service.premiumDate == Service.DEFAULT_PREMIUM_DATE) {
            WorkWithTimeApi.getSysdateLong() + (86400000 * 7).toLong()
        } else {
            WorkWithTimeApi.getMillisecondsStringDateWithSeconds(service.premiumDate) + (86400000 * 7).toLong()
        }
        return WorkWithTimeApi.getDateInFormatYMDHMS(Date(sysdateLong))
    }

    override fun returnUpdatedCallback(obj: Service) {
        checkPremiumPresenterCallback.showPremiumActivated(obj)
    }
}