package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

@Dao
interface ServiceDao {

    @Query("SELECT * FROM service")
    suspend fun findAll(): List<Service>

    @Query("SELECT * FROM service WHERE id = :id")
    suspend fun findById(id: String): Service?

    @Query("SELECT * FROM service WHERE userId = :userId")
    suspend fun findAllByUserId(userId: String?): List<Service>

    @Insert
    suspend fun insert(service: Service)

    @Update
    suspend fun update(service: Service)

    @Delete
    suspend fun delete(service: Service)

    @Query("DELETE FROM service")
    suspend fun deleteAll()
}