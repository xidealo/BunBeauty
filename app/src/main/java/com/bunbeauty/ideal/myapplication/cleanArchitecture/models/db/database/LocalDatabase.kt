package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.entity.User
import javax.inject.Inject
import android.icu.lang.UCharacter.GraphemeClusterBreak.V


@Database(entities = [User::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}
