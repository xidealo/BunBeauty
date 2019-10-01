package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class User(@PrimaryKey var id:String = "", var name: String ="", var city:String= "", var phone:String ="",
                var rating:Float = 0f, var countOfRates:Long = 0, var photoLink:String = "",
                var subscribersCount:Long = 0, var subscriptionsCount:Long = 0 ) {

    companion object {
        val PHONE = "phone"
        val USERS = "users"

        val NAME = "name"
        val CITY = "city"
        val PHOTO_LINK = "photo link"
        val AVG_RATING = "avg rating"
        val COUNT_OF_RATES = "count of rates"
    }
}
