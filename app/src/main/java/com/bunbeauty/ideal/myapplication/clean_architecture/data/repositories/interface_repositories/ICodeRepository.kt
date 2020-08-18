package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.code.GetCodeCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.code.UpdateCodeCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Code

interface ICodeRepository {
    fun insert(code: Code)
    fun delete(code: Code)
    fun update(code: Code,codeCallback: UpdateCodeCallback)
    fun get(): List<Code>

    fun getByCode(codeString:String, codeSubscriber: GetCodeCallback)
}