package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Order(
    @PrimaryKey
    var id: String = "",
    var masterId: String = "",
    var userId: String = "",
    var serviceId: String = "",
    var time: Long = 0
) {
    companion object {
        const val ORDERS = "orders"
        const val CLIENT_ID = "client id"
        const val MASTER_ID = "master id"
        const val SERVICE_ID = "master id"
        const val TIME = "time"
    }
}
