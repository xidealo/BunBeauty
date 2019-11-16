package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

@Dao
abstract class ServiceDao {

    @Query("SELECT * FROM service")
    abstract suspend fun findAll(): List<Service>

    @Query("SELECT * FROM service WHERE id = :id")
    abstract suspend fun findById(id: String): Service?

    @Query("SELECT * FROM service ORDER BY cost DESC LIMIT 1 ")
    abstract suspend fun findMaxCostService(): Service

    @Query("SELECT * FROM service ORDER BY countOfRates desc LIMIT 1 ")
    abstract suspend fun findMaxCountOfRatesService(): Service

    @Query("SELECT * FROM service WHERE userId = :userId ORDER BY creationDate")
    abstract suspend fun findAllByUserId(userId: String?): List<Service>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(service: Service)

    @Insert
    abstract suspend fun insertPhotos(photos: List<Photo>)

    @Update
    abstract suspend fun update(service: Service)

    @Delete
    abstract suspend fun delete(service: Service)

    @Query("DELETE FROM service")
    abstract suspend fun deleteAll()
}