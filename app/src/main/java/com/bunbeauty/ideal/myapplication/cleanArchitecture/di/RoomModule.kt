package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.app.Application
import androidx.room.Room
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.LocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.UserDataSource
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.UserRepo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/*
@Module
abstract class RoomModule(private val app: Application) {

    val localDatabase: LocalDatabase = Room
            .databaseBuilder(app, LocalDatabase::class.java, "local-db")
            .build()

    @Provides
    internal fun providesRoomDatabase(): LocalDatabase {
        return localDatabase
    }

    @Module
    companion object {


        /*@JvmStatic
        @Provides
        fun providesRoomDatabase(): LocalDatabase {
            return localDatabase
        }*/

        @JvmStatic
        @Provides
        fun provideAuthorizationPresenter(): AuthorizationPresenter = AuthorizationPresenter()

    }
}*/

@Module
class RoomModule(private val app: Application) {

    val localDatabase: LocalDatabase = Room
            .databaseBuilder(app, LocalDatabase::class.java, "local-db")
            .build()

    @Provides
    @Singleton
    internal fun providesApplication(): Application {
        return app
    }

    @Singleton
    @Provides
    internal fun providesRoomDatabase(): LocalDatabase {
        return localDatabase
    }

    @Singleton
    @Provides
    internal fun providesUserDao(localDatabase: LocalDatabase): UserDao {
        return localDatabase.getUserDao()
    }

    @Singleton
    @Provides
    internal fun providesUserRepo(userDao: UserDao): UserRepo {
        return UserDataSource(userDao)
    }

}
