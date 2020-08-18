package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Tag

@Dao
interface TagDao:BaseDao<Tag> {

    @Query("DELETE FROM tag")
    suspend fun deleteAll()
}