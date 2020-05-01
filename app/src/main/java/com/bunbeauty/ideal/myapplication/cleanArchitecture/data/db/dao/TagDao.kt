package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag

@Dao
interface TagDao:BaseDao<Tag> {

    @Query("DELETE FROM tag")
    suspend fun deleteAll()
}