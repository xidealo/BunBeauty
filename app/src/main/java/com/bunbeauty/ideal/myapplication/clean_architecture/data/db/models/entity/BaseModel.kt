package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity

import androidx.room.Ignore
import androidx.room.PrimaryKey

abstract class BaseModel(
    @Ignore

    open var id: String = ""
)
