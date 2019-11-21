package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ICodeCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Code

interface ICodeRepository {
    fun insert(code: Code)
    fun delete(code: Code)
    fun update(code: Code)
    fun get(): List<Code>

    fun getByCode(codeString:String, codeSubscriber: ICodeCallback)
}