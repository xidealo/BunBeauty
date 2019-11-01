package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Photo

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo)

    @Update
    suspend fun update(photo: Photo)

    @Delete
    suspend fun delete(photo: Photo)
}