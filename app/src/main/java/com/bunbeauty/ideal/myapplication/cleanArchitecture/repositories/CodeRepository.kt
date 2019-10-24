package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.CodeFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.CodeDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Code
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.ICodeRepository

class CodeRepository(codeDao: CodeDao, codeFirebase: CodeFirebase) : BaseRepository(), ICodeRepository {

    override fun insert(code: Code) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(code: Code) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(code: Code) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(): List<Code> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByCode(code: String) {

    }

}