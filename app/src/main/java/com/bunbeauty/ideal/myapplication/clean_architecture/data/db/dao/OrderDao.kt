package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dao

import androidx.room.Query
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order

interface OrderDao : BaseDao<Order> {
    @Query("SELECT * FROM `order`")
    suspend fun get(): List<Order>
}