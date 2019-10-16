package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.FileDescriptor
import java.net.Inet4Address

@Entity
data class Service(
        @PrimaryKey
        var id:String = "",
        var userId:String = "",
        var name: String ="",
        var address:String= "",
        var description: String= "",
        var category: String= "",
        var rating:Float = 0f,
        var countOfRates:Long = 0,
        var cost:Long = 0,
        var creationDate: String= "",
        var premiumDate: String= ""){

    companion object {
        const val SERVICES = "services"
        const val USER_ID = "user_id"
        const val NAME = "name"
        const val ADDRESS = "address"
        const val DESCRIPTION = "description"
        const val CATEGORY = "category"
        const val COST = "cost"
        const val AVG_RATING = "avg rating"
        const val COUNT_OF_RATES = "count of rates"
        const val CREATION_DATE = "creation date"
        const val PREMIUM_DATE = "premium date"
    }
}
