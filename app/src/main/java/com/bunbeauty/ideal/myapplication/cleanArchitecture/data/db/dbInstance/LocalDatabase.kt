package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dbInstance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.*

@Database(entities = [User::class,
    Service::class,
    Tag::class,
    WorkingDays::class,
    WorkingTime::class,
    Order::class,
    Photo::class,
    Review::class,
    Code::class], version = 21)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getServiceDao(): ServiceDao
    abstract fun getTagDao(): TagDao
    abstract fun getPhotoDao(): PhotoDao
    abstract fun getCodeDao(): CodeDao

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
