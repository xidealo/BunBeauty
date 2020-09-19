package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.code.GetCodeCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.code.UpdateCodeCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.CodeFirebase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dao.CodeDao
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Code
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.ICodeRepository
import kotlinx.coroutines.launch

class CodeRepository(private val codeFirebase: CodeFirebase) :
    BaseRepository(), ICodeRepository, GetCodeCallback, UpdateCodeCallback {

    lateinit var codeSubscriber: GetCodeCallback
    lateinit var updateCodeCallback: UpdateCodeCallback

    override fun insert(code: Code) {}

    override fun delete(code: Code) {}

    override fun update(code: Code, codeCallback: UpdateCodeCallback) {
        this.updateCodeCallback = codeCallback
        codeFirebase.update(code, codeCallback)
    }

    override fun get(): List<Code> {
        return listOf()
    }

    override fun getByCode(codeString: String, codeSubscriber: GetCodeCallback) {
        this.codeSubscriber = codeSubscriber
        codeFirebase.getByCode(codeString, this)
    }

    override fun returnList(objects: List<Code>) {
        when (objects.first().codeStatus) {
            Code.WRONG_CODE -> codeSubscriber.returnList(objects)
            Code.OLD_CODE -> codeSubscriber.returnList(objects)
            else -> {
                codeSubscriber.returnList(objects)
                launch {
                    //codeDao.insert(objects.first())
                }
            }
        }
    }

    override fun returnUpdatedCallback(obj: Code) {
        updateCodeCallback.returnUpdatedCallback(obj)
    }

}
