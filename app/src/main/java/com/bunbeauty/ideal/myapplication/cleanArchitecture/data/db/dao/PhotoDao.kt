package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo

@Dao
interface PhotoDao:BaseDao<Photo> {

    @Query("SELECT * FROM photo WHERE serviceId = :serviceId")
    suspend fun findAllByServiceId(serviceId: String): List<Photo>
}