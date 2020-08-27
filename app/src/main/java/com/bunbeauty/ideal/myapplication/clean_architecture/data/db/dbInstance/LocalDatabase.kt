package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dbInstance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dao.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Schedule
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime

@Database(
    entities = [
        User::class,
        Service::class,
        Tag::class,
        Order::class,
        Schedule::class,
        WorkingTime::class,
        Photo::class,
        Code::class,
        Dialog::class,
        Message::class,
        Subscriber::class,
        Subscription::class,
        ServiceComment::class,
        UserComment::class], version = 42
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getServiceDao(): ServiceDao
    abstract fun getTagDao(): TagDao
    abstract fun getPhotoDao(): PhotoDao
    abstract fun getCodeDao(): CodeDao
    abstract fun getDialogDao(): DialogDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "NoteDatabase"
                ).fallbackToDestructiveMigration().build()
                // ^^^ Убрать ^^^

                INSTANCE = instance
                return instance
            }
        }
    }
}
