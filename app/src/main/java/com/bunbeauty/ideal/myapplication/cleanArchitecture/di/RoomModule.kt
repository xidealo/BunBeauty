package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.app.Application
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.LocalDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule(private val app: Application) {
    @Singleton
    @Provides
    fun provideUserDao(): UserDao = LocalDatabase.getDatabase(app).getUserDao()
}
