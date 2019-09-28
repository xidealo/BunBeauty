package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class User {

    @PrimaryKey
    var id: Long = 0

    var phone: String? = null

    var userName: String? = null

    var city: String? = null

    var rating: Float? = null

    var subscriptionsCount: Long? = 0

    var subscribersCount: Long? = 0

    var ratesCount: Long? = 0
}