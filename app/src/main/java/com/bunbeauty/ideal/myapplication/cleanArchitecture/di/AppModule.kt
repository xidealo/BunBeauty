package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.database.LocalDatabase
import dagger.Module
import dagger.Provides
import androidx.room.Room
import android.content.Context

@Module
class AppModule {
    @Provides
    fun provideDatabase(context: Context, name: String) : LocalDatabase =
            Room.databaseBuilder(context, LocalDatabase::class.java, name).build()
}