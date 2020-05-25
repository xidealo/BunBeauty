package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order

class OrderFirebase {
    fun insert(order: Order) {}
    fun delete(order: Order) {}
    fun update(order: Order) {}
    fun get(order: Order) {}

    fun getIdForNew(userId: String): String {return "" }
//TODO// IMPLEMENTED
}