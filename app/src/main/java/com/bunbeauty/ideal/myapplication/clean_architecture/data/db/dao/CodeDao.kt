package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Code

@Dao
interface CodeDao : BaseDao<Code> {
    @Query("SELECT * FROM code WHERE code = :code")
    suspend fun getByCode(code: String): Code?
}