package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

@Database(entities = [User::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}
