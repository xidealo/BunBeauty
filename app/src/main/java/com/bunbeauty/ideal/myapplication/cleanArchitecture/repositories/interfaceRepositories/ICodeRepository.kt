package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ICodeSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Code

interface ICodeRepository {
    fun insert(code: Code)
    fun delete(code: Code)
    fun update(code: Code)
    fun get(): List<Code>

    fun getByCode(codeString:String, codeSubscriber: ICodeSubscriber)
}