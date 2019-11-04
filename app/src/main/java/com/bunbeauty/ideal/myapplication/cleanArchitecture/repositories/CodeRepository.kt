package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ICodeSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.CodeFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.CodeDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Code
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.ICodeRepository
import kotlinx.coroutines.launch

class CodeRepository(private val codeDao: CodeDao, private val codeFirebase: CodeFirebase) : BaseRepository(), ICodeRepository, ICodeSubscriber {

    lateinit var codeSubscriber: ICodeSubscriber

    override fun insert(code: Code) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(code: Code) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(code: Code) {
        /*launch {
            codeDao.update(code)
        }*/
        codeFirebase.update(code)
    }

    override fun get(): List<Code> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByCode(codeString: String, codeSubscriber: ICodeSubscriber) {
        this.codeSubscriber = codeSubscriber
        codeFirebase.getByCode(codeString, this)
    }

    override fun returnCode(code: Code) {
        when (code.codeStatus) {
            Code.WRONG_CODE -> codeSubscriber.returnCode(code)
            Code.OLD_CODE -> codeSubscriber.returnCode(code)
            else -> {
                codeSubscriber.returnCode(code)
                launch {
                    codeDao.insert(code)
                }
            }
        }
    }

}
