package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo

@Dao
interface PhotoDao:BaseDao<Photo> {

    @Query("SELECT * FROM photo WHERE serviceId = :serviceId")
    suspend fun findAllByServiceId(serviceId: String): List<Photo>
}