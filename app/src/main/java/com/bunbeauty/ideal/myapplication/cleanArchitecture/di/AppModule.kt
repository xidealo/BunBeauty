package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.app.Application
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dbInstance.LocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: Application) {

    @Provides
    fun provideUserFirebaseApi(): UserFirebaseApi = UserFirebaseApi()

    @Provides
    fun provideUserDao(): UserDao = LocalDatabase.getDatabase(app).getUserDao()

    @Provides
    fun provideUserRepository(userDao: UserDao, userFirebaseApi: UserFirebaseApi): UserRepository = UserRepository(userDao, userFirebaseApi)

    @Provides
    fun provideAuthorizationInteractor(userDao: UserDao): AuthorizationInteractor = AuthorizationInteractor(userDao)

    @Provides
    fun provideVerifyPhoneInteractor(userRepository: UserRepository): VerifyPhoneInteractor = VerifyPhoneInteractor(userRepository)

    @Provides
    fun provideRegistrationInteractor(userRepository: UserRepository): RegistrationInteractor = RegistrationInteractor(userRepository)

}