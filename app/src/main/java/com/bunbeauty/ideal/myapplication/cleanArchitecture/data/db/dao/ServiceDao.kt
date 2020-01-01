package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

@Dao
interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: Service)

    @Insert
    suspend fun insertPhotos(photos: List<Photo>)

    @Update
    suspend fun update(service: Service)

    @Delete
    suspend fun delete(service: Service)

    @Query("SELECT * FROM service")
    suspend fun get(): List<Service>

    @Query("SELECT * FROM service WHERE id = :id")
    suspend fun getById(id: String): Service

    @Query("SELECT * FROM service ORDER BY cost DESC LIMIT 1 ")
    suspend fun getMaxCostService(): Service

    @Query("SELECT * FROM service ORDER BY countOfRates desc LIMIT 1 ")
    suspend fun getMaxCountOfRatesService(): Service

    @Query("SELECT * FROM service WHERE userId = :userId ORDER BY creationDate")
    suspend fun getAllByUserId(userId: String): List<Service>

    @Query("SELECT * FROM service WHERE userId = :userId AND name = :name ORDER BY creationDate")
    suspend fun getAllByUserIdAndServiceName(userId: String, name: String): List<Service>

    @Query("DELETE FROM service")
    suspend fun deleteAll()
}