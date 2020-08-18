package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity

import androidx.room.Embedded

data class MainScreenData(
    val weight: Float = 0f,
    @Embedded
    val user: User = User(),
    @Embedded
    val service: Service = Service()
)