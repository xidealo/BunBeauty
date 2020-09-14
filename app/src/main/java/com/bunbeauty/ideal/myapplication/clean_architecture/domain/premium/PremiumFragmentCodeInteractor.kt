package com.bunbeauty.ideal.myapplication.clean_architecture.domain.premium

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.PremiumFragmentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.code.GetCodeCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.code.UpdateCodeCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Code
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.CodeRepository

class PremiumFragmentCodeInteractor(private val codeRepository: CodeRepository) :
    GetCodeCallback, UpdateCodeCallback {

    private lateinit var premiumFragmentPresenterCallback: PremiumFragmentPresenterCallback

    fun checkCode(
        code: String,
        premiumFragmentPresenterCallback: PremiumFragmentPresenterCallback
    ) {
        this.premiumFragmentPresenterCallback = premiumFragmentPresenterCallback
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
            Code.OLD_CODE -> premiumFragmentPresenterCallback.showError("Код больше не действителен")
            Code.WRONG_CODE -> premiumFragmentPresenterCallback.showError("Неверно введен код")
        }
    }

    override fun returnUpdatedCallback(obj: Code) {
        premiumFragmentPresenterCallback.activatePremium()
    }

}