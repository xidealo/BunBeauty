package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

@Database(entities = [User::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}
