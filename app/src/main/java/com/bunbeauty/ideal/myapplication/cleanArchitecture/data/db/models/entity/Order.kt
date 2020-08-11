package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Session

@Entity
data class Order(
    @PrimaryKey
    var id: String = "",
    var clientId: String,
    var masterId: String,
    var serviceId: String,
    var serviceName: String,
    @Embedded var session: Session
) {
    companion object {
        const val ORDERS = "orders"
        const val MASTER_ID = "master id"
        const val SERVICE_ID = "service id"
        const val SERVICE_NAME = "service name"
    }
}
