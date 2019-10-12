package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.app.Application
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.LocalDatabase
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: Application) {

    @Provides
    fun provideUserDao(): UserDao = LocalDatabase.getDatabase(app).getUserDao()
    @Provides
    fun provideAuthorizationInteractor(userDao: UserDao): AuthorizationInteractor = AuthorizationInteractor(userDao)
    @Provides
    fun provideVerifyPhoneInteractor(): VerifyPhoneInteractor = VerifyPhoneInteractor()

}

/*@Module
class AppModule() {

    @Provides
    @Singleton
    internal fun providesApplication(): Application {
        return app
    }
    @Provides
    @Singleton
    internal fun provideAuthorizationInteractor() : AuthorizationInteractor = AuthorizationInteractor()

    @Provides
    @Singleton
    internal fun provideAuthorizationPresenter() : AuthorizationPresenter = AuthorizationPresenter()

}*/
