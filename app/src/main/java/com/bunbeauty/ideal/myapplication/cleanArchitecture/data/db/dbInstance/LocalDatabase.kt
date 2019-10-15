package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dbInstance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

@Database(entities = [User::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

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
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}