package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.CheckPremiumCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ICodeSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Code
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.CodeRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository

class PremiumElementInteractor(val serviceRepository: ServiceRepository, private val codeRepository: CodeRepository) : ICodeSubscriber {

    lateinit var checkPremiumCallback: CheckPremiumCallback

    fun checkCode(code: String, checkPremiumCallback: CheckPremiumCallback) {
        this.checkPremiumCallback = checkPremiumCallback
        //проверка кода
        codeRepository.getByCode(code, this)
    }

    override fun returnCode(code: Code) {
        when (code.codeStatus) {
            Code.PREMIUM_ACTIVATED -> {
                //изменить количество и статус кода
                activatePremium(code)
                code.count = decrement(code.count)

                if(code.count == "0"){
                    code.codeStatus = Code.OLD_CODE
                }

                codeRepository.update(code)
                checkPremiumCallback.showPremiumActivated()
            }
            Code.OLD_CODE -> checkPremiumCallback.showError("Код больше не действителен")
            Code.WRONG_CODE -> checkPremiumCallback.showError("Неверно введен код")
        }
    }

    private fun activatePremium(code: Code) {

    }
    private fun decrement(count: String): String = (count.toInt() - 1).toString()

    fun addSevenDayPremium(serviceId: String): String {
        /*var sysdateLong = WorkWithTimeApi.getMillisecondsStringDateWithSeconds(date)
        //86400000 - day * 7 day
        sysdateLong += (86400000 * 7).toLong()*/
        return ""//WorkWithTimeApi.getDateInFormatYMDHMS(Date(sysdateLong))
    }

}