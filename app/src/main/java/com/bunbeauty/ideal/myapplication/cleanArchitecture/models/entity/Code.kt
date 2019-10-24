package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Code(
        @PrimaryKey
        var id: String = "",
        var code: String = "",
        var count:String = ""){
        companion object {
                const val CODES = "codes"
                const val CODE = "code"
                const val COUNT = "count"
        }
}