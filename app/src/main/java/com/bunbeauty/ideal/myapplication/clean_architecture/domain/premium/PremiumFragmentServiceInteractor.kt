package com.bunbeauty.ideal.myapplication.clean_architecture.domain.premium

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.PremiumFragmentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.GetServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository

class PremiumFragmentServiceInteractor(val serviceRepository: IServiceRepository) :
    UpdateServiceCallback, GetServiceCallback {

    lateinit var service: Service
    lateinit var premiumFragmentPresenterCallback: PremiumFragmentPresenterCallback

    fun activatePremium(
        service: Service,
        premiumFragmentPresenterCallback: PremiumFragmentPresenterCallback
    ) {
        this.premiumFragmentPresenterCallback = premiumFragmentPresenterCallback
        serviceRepository.getById(service.id, service.userId, true, this)
    }

    override fun returnGottenObject(obj: Service?) {
        if (obj == null) return
        serviceRepository.updatePremium(obj, SEVEN_DAYS, this)
    }


    override fun returnUpdatedCallback(obj: Service) {
        premiumFragmentPresenterCallback.showPremiumActivated(obj)
    }

    companion object {
        const val SEVEN_DAYS: Long = 86400000 * 7
    }
}