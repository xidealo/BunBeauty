package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

@Dao
interface ServiceDao {

    @Query("SELECT * FROM service")
    suspend fun findAll(): List<Service>

    @Query("SELECT * FROM service WHERE id = :id")
    suspend fun findById(id: String): Service?

    @Query("SELECT * FROM service ORDER BY cost DESC LIMIT 1 ")
    suspend fun findMaxCostService(): Service

    @Query("SELECT * FROM service ORDER BY countOfRates desc LIMIT 1 ")
    suspend fun findMaxCountOfRatesService(): Service

    @Query("SELECT * FROM service WHERE userId = :userId ORDER BY creationDate")
    suspend fun findAllByUserId(userId: String?): List<Service>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: Service)

    @Update
    suspend fun update(service: Service)

    @Delete
    suspend fun delete(service: Service)

    @Query("DELETE FROM service")
    suspend fun deleteAll()
}