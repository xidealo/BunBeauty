package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.LocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.UserRepo
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(dependencies = [], modules = [RoomModule::class])
interface RoomComponent {
    fun userDao(): UserDao

    fun localDatabase(): LocalDatabase

    fun userRepo(): UserRepo
}