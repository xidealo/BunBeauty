package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog

@Dao
interface DialogDao:BaseDao<Dialog> {
    @Query("SELECT * FROM dialog")
    suspend fun get(): List<Dialog>

    @Query("SELECT * FROM dialog WHERE user_id = :userId")
    suspend fun getByUserId(userId: String): List<Dialog>
}