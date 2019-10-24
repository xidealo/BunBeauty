package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.app.Application
import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.AddingServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.PhotoFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.TagFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.PhotoDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.TagDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dbInstance.LocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.PhotoRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.TagRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: Application, private val intent: Intent) {
    //FIREBASE API
    @Provides
    fun provideUserFirebaseApi(): UserFirebaseApi = UserFirebaseApi()
    @Provides
    fun provideServiceFirebaseApi(): ServiceFirebaseApi = ServiceFirebaseApi()
    @Provides
    fun provideTagFirebase(): TagFirebase = TagFirebase()
    @Provides
    fun providePhotoFirebase(): PhotoFirebase= PhotoFirebase()

    // DAO
    @Provides
    fun provideUserDao(): UserDao = LocalDatabase.getDatabase(app).getUserDao()
    @Provides
    fun provideServiceDao(): ServiceDao = LocalDatabase.getDatabase(app).getServiceDao()
    @Provides
    fun provideTagDao(): TagDao = LocalDatabase.getDatabase(app).getTagDao()
    @Provides
    fun providePhotoDao(): PhotoDao = LocalDatabase.getDatabase(app).getPhotoDao()

    //REPOSITORIES
    @Provides
    fun provideUserRepository(userDao: UserDao, userFirebaseApi: UserFirebaseApi): UserRepository = UserRepository(userDao, userFirebaseApi)
    @Provides
    fun provideServiceRepository(serviceDao: ServiceDao, serviceFirebaseApi: ServiceFirebaseApi): ServiceRepository = ServiceRepository(serviceDao, serviceFirebaseApi)
    @Provides
    fun provideTagRepository(tagDao: TagDao, tagFirebase: TagFirebase): TagRepository = TagRepository(tagDao,tagFirebase)
    @Provides
    fun providePhotoRepository(photoDao: PhotoDao, photoFirebase: PhotoFirebase): PhotoRepository = PhotoRepository(photoDao,photoFirebase)

    // INTERACTORS
    @Provides
    fun provideAuthorizationInteractor(userRepository: UserRepository): AuthorizationInteractor = AuthorizationInteractor(userRepository, intent)
    @Provides
    fun provideVerifyPhoneInteractor(userRepository: UserRepository): VerifyPhoneInteractor = VerifyPhoneInteractor(userRepository, intent)
    @Provides
    fun provideRegistrationInteractor(userRepository: UserRepository): RegistrationInteractor = RegistrationInteractor(userRepository, intent)
    @Provides
    fun provideProfileInteractor(userRepository: UserRepository, serviceRepository: ServiceRepository): ProfileInteractor = ProfileInteractor(userRepository,serviceRepository, intent)
    @Provides
    fun provideAddingServiceInteractor(serviceRepository: ServiceRepository, tagRepository: TagRepository, photoRepository: PhotoRepository): AddingServiceInteractor = AddingServiceInteractor(serviceRepository, tagRepository,photoRepository)
}