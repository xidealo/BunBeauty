package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Photo

@Dao
interface PhotoDao {
    @Insert
    suspend fun insert(photo: Photo)

    @Update
    suspend fun update(photo: Photo)

    @Delete
    suspend fun delete(photo: Photo)
}