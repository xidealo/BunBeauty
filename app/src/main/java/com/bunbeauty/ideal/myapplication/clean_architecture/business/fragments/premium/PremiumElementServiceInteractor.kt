package com.bunbeauty.ideal.myapplication.clean_architecture.business.fragments.premium

import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.CheckPremiumPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository

class PremiumElementServiceInteractor(val serviceRepository: IServiceRepository) :
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

    private fun addSevenDayPremium(service: Service): Long {
        val sysdateLong: Long = if (service.premiumDate == Service.DEFAULT_PREMIUM_DATE) {
            WorkWithTimeApi.getSysdateLong() + (86400000 * 7)
        } else {
            service.premiumDate + (86400000 * 7)
        }
        return sysdateLong
    }

    override fun returnUpdatedCallback(obj: Service) {
        checkPremiumPresenterCallback.showPremiumActivated(obj)
    }
}