package com.bunbeauty.ideal.myapplication.clean_architecture.business.fragments.premium

import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.CheckPremiumPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.GetServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository

class PremiumElementServiceInteractor(val serviceRepository: IServiceRepository) :
    UpdateServiceCallback, GetServiceCallback {

    lateinit var service: Service
    lateinit var checkPremiumPresenterCallback: CheckPremiumPresenterCallback

    fun activatePremium(
        service: Service,
        checkPremiumPresenterCallback: CheckPremiumPresenterCallback
    ) {
        this.checkPremiumPresenterCallback = checkPremiumPresenterCallback
        serviceRepository.getById(service.id, service.userId, true, this)
    }

    override fun returnGottenObject(obj: Service?) {
        if (obj == null) return
        serviceRepository.updatePremium(obj, SEVEN_DAYS, this)
    }


    override fun returnUpdatedCallback(obj: Service) {
        checkPremiumPresenterCallback.showPremiumActivated(obj)
    }

    companion object {
        const val SEVEN_DAYS: Long = 86400000 * 7
    }
}