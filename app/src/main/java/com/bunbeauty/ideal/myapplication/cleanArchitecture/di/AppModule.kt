package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.app.Application
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.AddingServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.TagFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.TagDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dbInstance.LocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.TagRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: Application) {
    //FIREBASE API
    @Provides
    fun provideUserFirebaseApi(): UserFirebaseApi = UserFirebaseApi()
    @Provides
    fun provideServiceFirebaseApi(): ServiceFirebaseApi = ServiceFirebaseApi()
    @Provides
    fun provideTagFirebase(): TagFirebase = TagFirebase()

    // DAO
    @Provides
    fun provideUserDao(): UserDao = LocalDatabase.getDatabase(app).getUserDao()
    @Provides
    fun provideServiceDao(): ServiceDao = LocalDatabase.getDatabase(app).getServiceDao()
    @Provides
    fun provideTagDao(): TagDao = LocalDatabase.getDatabase(app).getTagDao()

    //REPOSITORIES
    @Provides
    fun provideUserRepository(userDao: UserDao, userFirebaseApi: UserFirebaseApi): UserRepository = UserRepository(userDao, userFirebaseApi)
    @Provides
    fun provideServiceRepository(serviceDao: ServiceDao, serviceFirebaseApi: ServiceFirebaseApi): ServiceRepository = ServiceRepository(serviceDao, serviceFirebaseApi)
    @Provides
    fun provideTagRepository(tagDao: TagDao, tagFirebase: TagFirebase): TagRepository = TagRepository(tagDao,tagFirebase)

    // INTERACTORS
    @Provides
    fun provideAuthorizationInteractor(userRepository: UserRepository): AuthorizationInteractor = AuthorizationInteractor(userRepository)
    @Provides
    fun provideVerifyPhoneInteractor(userRepository: UserRepository): VerifyPhoneInteractor = VerifyPhoneInteractor(userRepository)
    @Provides
    fun provideRegistrationInteractor(userRepository: UserRepository): RegistrationInteractor = RegistrationInteractor(userRepository)
    @Provides
    fun provideProfileInteractor(userRepository: UserRepository, serviceRepository: ServiceRepository): ProfileInteractor = ProfileInteractor(userRepository,serviceRepository)
    @Provides
    fun provideAddingServiceInteractor(serviceRepository: ServiceRepository, tagRepository: TagRepository): AddingServiceInteractor = AddingServiceInteractor(serviceRepository, tagRepository)
}