package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.app.Application
import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.AddingServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.PremiumElementInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.SearchServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dbInstance.LocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.*
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: Application, private val intent: Intent) {

    // FIREBASE API
    @Provides
    fun provideUserFirebaseApi(): UserFirebaseApi = UserFirebaseApi()
    @Provides
    fun provideServiceFirebaseApi(): ServiceFirebaseApi = ServiceFirebaseApi()
    @Provides
    fun provideTagFirebase(): TagFirebase = TagFirebase()
    @Provides
    fun providePhotoFirebase(): PhotoFirebase= PhotoFirebase()
    @Provides
    fun provideCodeFirebase(): CodeFirebase = CodeFirebase()

    // DAO
    @Provides
    fun provideUserDao(): UserDao = LocalDatabase.getDatabase(app).getUserDao()
    @Provides
    fun provideServiceDao(): ServiceDao = LocalDatabase.getDatabase(app).getServiceDao()
    @Provides
    fun provideTagDao(): TagDao = LocalDatabase.getDatabase(app).getTagDao()
    @Provides
    fun providePhotoDao(): PhotoDao = LocalDatabase.getDatabase(app).getPhotoDao()
    @Provides
    fun provideCodeDao(): CodeDao = LocalDatabase.getDatabase(app).getCodeDao()

    //REPOSITORIES
    @Provides
    fun provideUserRepository(userDao: UserDao, userFirebaseApi: UserFirebaseApi): UserRepository =
            UserRepository(userDao, userFirebaseApi)
    @Provides
    fun provideServiceRepository(serviceDao: ServiceDao, serviceFirebaseApi: ServiceFirebaseApi): ServiceRepository =
            ServiceRepository(serviceDao, serviceFirebaseApi)
    @Provides
    fun provideTagRepository(tagDao: TagDao, tagFirebase: TagFirebase): TagRepository =
            TagRepository(tagDao,tagFirebase)
    @Provides
    fun providePhotoRepository(photoDao: PhotoDao, photoFirebase: PhotoFirebase): PhotoRepository =
            PhotoRepository(photoDao,photoFirebase)
    @Provides
    fun provideCodeRepository(codeDao: CodeDao, codeFirebase: CodeFirebase): CodeRepository =
            CodeRepository(codeDao,codeFirebase)

    // INTERACTORS
    @Provides
    fun provideAuthorizationInteractor(userRepository: UserRepository): AuthorizationInteractor =
            AuthorizationInteractor(userRepository, intent)
    @Provides
    fun provideVerifyPhoneInteractor(userRepository: UserRepository): VerifyPhoneInteractor =
            VerifyPhoneInteractor(userRepository, intent)
    @Provides
    fun provideRegistrationInteractor(userRepository: UserRepository): RegistrationInteractor =
            RegistrationInteractor(userRepository, intent)
    @Provides
    fun provideProfileInteractor(userRepository: UserRepository, serviceRepository: ServiceRepository): ProfileInteractor =
            ProfileInteractor(userRepository,serviceRepository, intent)
    @Provides
    fun provideAddingServiceInteractor(serviceRepository: ServiceRepository, tagRepository: TagRepository, photoRepository: PhotoRepository): AddingServiceInteractor =
            AddingServiceInteractor(serviceRepository, tagRepository,photoRepository)
    @Provides
    fun providePremiumElementInteractor(serviceRepository: ServiceRepository, codeRepository: CodeRepository): PremiumElementInteractor =
            PremiumElementInteractor(serviceRepository,codeRepository)
    @Provides
    fun provideMainScreenInteractor(userRepository: UserRepository,serviceRepository: ServiceRepository): MainScreenInteractor =
            MainScreenInteractor(userRepository,serviceRepository)
    @Provides
    fun provideServiceInteractor(photoRepository: PhotoRepository): ServiceInteractor =
            ServiceInteractor(photoRepository, intent)
    @Provides
    fun provideSearchServiceInteractor(userRepository: UserRepository): SearchServiceInteractor =
            SearchServiceInteractor(userRepository)
}