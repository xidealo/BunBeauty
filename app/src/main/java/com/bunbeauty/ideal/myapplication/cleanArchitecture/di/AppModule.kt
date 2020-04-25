package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.app.Application
import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementCodeInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.SearchServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenUserInteractor
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
    fun provideUserFirebaseApi() = UserFirebaseApi()

    @Provides
    fun provideServiceFirebaseApi() = ServiceFirebaseApi()

    @Provides
    fun provideTagFirebase() = TagFirebase()

    @Provides
    fun providePhotoFirebase() = PhotoFirebase()

    @Provides
    fun provideCodeFirebase() = CodeFirebase()

    // DAO
    @Provides
    fun provideUserDao() = LocalDatabase.getDatabase(app).getUserDao()

    @Provides
    fun provideServiceDao() = LocalDatabase.getDatabase(app).getServiceDao()

    @Provides
    fun provideTagDao() = LocalDatabase.getDatabase(app).getTagDao()

    @Provides
    fun providePhotoDao() = LocalDatabase.getDatabase(app).getPhotoDao()

    @Provides
    fun provideCodeDao() = LocalDatabase.getDatabase(app).getCodeDao()

    //REPOSITORIES
    @Provides
    fun provideUserRepository(userDao: UserDao, userFirebaseApi: UserFirebaseApi) =
        UserRepository(userDao, userFirebaseApi)

    @Provides
    fun provideServiceRepository(serviceDao: ServiceDao, serviceFirebaseApi: ServiceFirebaseApi) =
        ServiceRepository(serviceDao, serviceFirebaseApi)

    @Provides
    fun provideTagRepository(tagDao: TagDao, tagFirebase: TagFirebase) =
        TagRepository(tagDao, tagFirebase)

    @Provides
    fun providePhotoRepository(photoDao: PhotoDao, photoFirebase: PhotoFirebase) =
        PhotoRepository(photoDao, photoFirebase)

    @Provides
    fun provideCodeRepository(codeDao: CodeDao, codeFirebase: CodeFirebase) =
        CodeRepository(codeDao, codeFirebase)

    // INTERACTORS
    @Provides
    fun provideAuthorizationInteractor(userRepository: UserRepository) =
        AuthorizationInteractor(userRepository)

    @Provides
    fun provideVerifyPhoneInteractor(userRepository: UserRepository) =
        VerifyPhoneInteractor(userRepository, intent)

    @Provides
    fun provideRegistrationInteractor(userRepository: UserRepository) =
        RegistrationInteractor(userRepository, intent)

    @Provides
    fun provideProfileInteractor(
        userRepository: UserRepository,
        serviceRepository: ServiceRepository
    ) =
        ProfileInteractor(userRepository, serviceRepository, intent)

    @Provides
    fun provideAddingServiceInteractor(
        serviceRepository: ServiceRepository,
        tagRepository: TagRepository,
        photoRepository: PhotoRepository
    ) =
        CreationServiceInteractor(serviceRepository, tagRepository, photoRepository)

    @Provides
    fun providePremiumElementCodeInteractor(codeRepository: CodeRepository) =
        PremiumElementCodeInteractor(
            codeRepository
        )

    @Provides
    fun providePremiumElementServiceInteractor(serviceRepository: ServiceRepository) =
        PremiumElementServiceInteractor(
            serviceRepository
        )

    @Provides
    fun provideMainScreenUserInteractor(
        userRepository: UserRepository
    ) =
        MainScreenUserInteractor(userRepository)

    @Provides
    fun provideMainScreenServiceInteractor(
        serviceRepository: ServiceRepository
    ) =
        MainScreenServiceInteractor(serviceRepository)

    @Provides
    fun provideMainScreenDataInteractor() =
        MainScreenDataInteractor()

    @Provides
    fun provideServiceInteractor(photoRepository: PhotoRepository) =
        ServiceInteractor(photoRepository, intent)

    @Provides
    fun provideSearchServiceInteractor(userRepository: UserRepository) =
        SearchServiceInteractor(userRepository)

    @Provides
    fun provideEditProfileInteractor(): EditProfileInteractor {
        return EditProfileInteractor(intent)
    }
}