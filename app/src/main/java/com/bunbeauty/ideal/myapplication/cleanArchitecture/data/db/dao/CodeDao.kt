package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Code

@Dao
interface CodeDao {
    @Insert
    suspend fun insert(code: Code)

    @Update
    suspend fun update(code: Code)

    @Delete
    suspend fun delete(code: Code)
}