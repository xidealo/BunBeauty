package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ICheckPremiumCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ICodeCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Code
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.CodeRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*

class PremiumElementInteractor(val serviceRepository: ServiceRepository, private val codeRepository: CodeRepository) :
        ICodeCallback, UpdateServiceCallback {

    lateinit var checkPremiumCallback: ICheckPremiumCallback
    lateinit var service: Service
    fun checkCode(code: String, checkPremiumCallback: ICheckPremiumCallback, service: Service) {
        this.checkPremiumCallback = checkPremiumCallback
        this.service = service
        //проверка кода
        codeRepository.getByCode(code, this)
    }

    override fun returnCode(code: Code) {
        when (code.codeStatus) {
            Code.PREMIUM_ACTIVATED -> {
                //изменить количество и статус кода
                activatePremium()
                code.count = decrement(code.count)

                if(code.count == "0"){
                    code.codeStatus = Code.OLD_CODE
                }
                //MAKE CALLBACK!
                codeRepository.update(code)
            }
            Code.OLD_CODE -> checkPremiumCallback.showError("Код больше не действителен")
            Code.WRONG_CODE -> checkPremiumCallback.showError("Неверно введен код")
        }
    }

    private fun activatePremium() {
        service.premiumDate = addSevenDayPremium(service)
        serviceRepository.update(service, this)
    }

    private fun decrement(count: String): String = (count.toInt() - 1).toString()

    private fun addSevenDayPremium(service: Service): String {
        val sysdateLong: Long = if(service.premiumDate == Service.DEFAULT_PREMIUM_DATE){
            WorkWithTimeApi.getSysdateLong() + (86400000 * 7).toLong()
        }else{
            WorkWithTimeApi.getMillisecondsStringDateWithSeconds(service.premiumDate) + (86400000 * 7).toLong()
        }
        return WorkWithTimeApi.getDateInFormatYMDHMS(Date(sysdateLong))
    }

    override fun returnUpdatedCallback(obj: Service) {
        checkPremiumCallback.showPremiumActivated()
    }

}