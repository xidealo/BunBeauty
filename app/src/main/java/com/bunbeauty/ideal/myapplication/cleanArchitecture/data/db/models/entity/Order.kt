package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Order (@PrimaryKey
             var id: String = "",
             var userId: String = "",
             var isCanceled: String = "",
             var messageTime: String = "",
             var workingTimeId: String = "" ){
    companion object {
        const val ORDERS = "orders"
        const val USER_ID = "user id"
        const val IS_CANCELED = "is canceled"
        const val MESSAGE_TIME = "message time"
        const val WORKING_TIME_ID = "working time id"
    }
}
