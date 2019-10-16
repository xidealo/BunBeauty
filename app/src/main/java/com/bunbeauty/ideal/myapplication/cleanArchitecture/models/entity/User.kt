package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
        @PrimaryKey
        var id:String = "",
        var name: String = "",
        var city:String = "",
        var phone:String ="",
        var rating:Float = 0f,
        var countOfRates:Long = 0,
        var photoLink:String = "",
        var subscribersCount:Long = 0,
        var subscriptionsCount:Long = 0 ) {

    companion object {
        const val USERS = "users"

        const val PHONE = "phone"
        const val NAME = "name"
        const val CITY = "city"
        const val PHOTO_LINK = "photo link"
        const val AVG_RATING = "avg rating"
        const val COUNT_OF_RATES = "count of rates"
        const val COUNT_OF_SUBSCRIBERS = "count of subscribers"
        const val COUNT_OF_SUBSCRIPTIONS = "count of subscriptions"
    }
}
