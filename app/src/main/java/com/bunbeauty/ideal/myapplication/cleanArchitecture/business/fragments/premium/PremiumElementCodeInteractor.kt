package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.CheckPremiumPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.code.GetCodeCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.code.UpdateCodeCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Code
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.CodeRepository

class PremiumElementCodeInteractor(private val codeRepository: CodeRepository) :
    GetCodeCallback, UpdateCodeCallback {

    lateinit var checkPremiumPresenterCallback: CheckPremiumPresenterCallback

    fun checkCode(
        code: String,
        checkPremiumPresenterCallback: CheckPremiumPresenterCallback
    ) {
        this.checkPremiumPresenterCallback = checkPremiumPresenterCallback
        codeRepository.getByCode(code, this)
    }

    override fun returnList(objects: List<Code>) {
        val code = objects.first()
        when (code.codeStatus) {
            Code.PREMIUM_ACTIVATED -> {
                //изменить количество и статус кода
                code.count = code.count - 1

                if (code.count == 0) {
                    code.codeStatus = Code.OLD_CODE
                }
                codeRepository.update(code, this)
            }
            Code.OLD_CODE -> checkPremiumPresenterCallback.showError("Код больше не действителен")
            Code.WRONG_CODE -> checkPremiumPresenterCallback.showError("Неверно введен код")
        }
    }

    override fun returnUpdatedCallback(obj: Code) {
        checkPremiumPresenterCallback.activatePremium()
    }

}