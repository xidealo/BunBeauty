package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.WorkWithTimeApi
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
@Parcelize
data class Service(

    var userId: String,
    var name: String,
    var duration: Float,
    var address: String,
    var description: String,
    var category: String,
    var rating: Float = 0f,
    var countOfRates: Long = 0,
    var cost: Long,
    var creationDate: Long = 0L,
    var premiumDate: Long = 0L,
    @Embedded
    var tags: ArrayList<Tag> = arrayListOf(),
    @Embedded
    var photos: ArrayList<Photo> = arrayListOf()
) : Parcelable, BaseModel() {

    companion object {

        fun checkPremium(premiumDate: Long): Boolean {
            val sysDate = Date().time
            return sysDate <= premiumDate
        }

        const val SERVICES = "services"
        const val SERVICE = "service"

        const val NAME = "name"
        const val SERVICE_PHOTO = "service photo"
        const val DURATION = "duration"
        const val ADDRESS = "address"
        const val DESCRIPTION = "description"
        const val CATEGORY = "category"
        const val COST = "cost"
        const val AVG_RATING = "avg rating"
        const val COUNT_OF_RATES = "count of rates"
        const val CREATION_DATE = "creation date"
        const val PREMIUM_DATE = "premium date"
        const val DEFAULT_PREMIUM_DATE = 0L
    }

    override fun describeContents(): Int {
        return 0
    }

}
