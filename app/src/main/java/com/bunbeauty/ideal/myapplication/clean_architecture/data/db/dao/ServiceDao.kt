package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

@Dao
interface ServiceDao : BaseDao<Service> {

    @Insert
    suspend fun insertPhotos(photos: List<Photo>)

    @Query("SELECT * FROM service")
    suspend fun get(): List<Service>

    @Query("SELECT * FROM service WHERE id = :id")
    suspend fun getById(id: String): List<Service>

    @Query("SELECT * FROM service WHERE userId = :userId ORDER BY creationDate")
    suspend fun getAllByUserId(userId: String): List<Service>

    @Query("DELETE FROM service")
    suspend fun deleteAll()
}