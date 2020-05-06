package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.app.Application
import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.FiguringServicePoints
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.SearchServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementCodeInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule.ScheduleInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.EditServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.SubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dbInstance.LocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.*
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: Application, private val intent: Intent) {

    // FIREBASE API
    @Provides
    fun provideUserFirebaseApi() = UserFirebase()

    @Provides
    fun provideServiceFirebaseApi() = ServiceFirebase()

    @Provides
    fun provideTagFirebase() = TagFirebase()

    @Provides
    fun providePhotoFirebase() = PhotoFirebase()

    @Provides
    fun provideCodeFirebase() = CodeFirebase()

    @Provides
    fun provideDialogFirebase() = DialogFirebase()

    @Provides
    fun provideMessageFirebase() = MessageFirebase()

    @Provides
    fun provideSubscriptionFirebase() = SubscriptionFirebase()

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

    @Provides
    fun provideDialogDao() = LocalDatabase.getDatabase(app).getDialogDao()

    //REPOSITORIES
    @Provides
    fun provideUserRepository(userDao: UserDao, userFirebase: UserFirebase) =
        UserRepository(userDao, userFirebase)

    @Provides
    fun provideServiceRepository(serviceDao: ServiceDao, serviceFirebase: ServiceFirebase) =
        ServiceRepository(serviceDao, serviceFirebase)

    @Provides
    fun provideTagRepository(tagDao: TagDao, tagFirebase: TagFirebase) =
        TagRepository(tagDao, tagFirebase)

    @Provides
    fun providePhotoRepository(photoDao: PhotoDao, photoFirebase: PhotoFirebase) =
        PhotoRepository(photoDao, photoFirebase)

    @Provides
    fun provideCodeRepository(codeDao: CodeDao, codeFirebase: CodeFirebase) =
        CodeRepository(codeDao, codeFirebase)

    @Provides
    fun provideDialogRepository(dialogDao: DialogDao, dialogFirebase: DialogFirebase) =
        DialogRepository(dialogDao, dialogFirebase)

    @Provides
    fun provideMessageRepository(messageFirebase: MessageFirebase) =
        MessageRepository(messageFirebase)

    @Provides
    fun provideSubscriptionRepository(subscriptionFirebase: SubscriptionFirebase) =
        SubscriptionRepository(subscriptionFirebase)

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
    fun provideProfileUserInteractor(
        userRepository: UserRepository
    ) =
        ProfileUserInteractor(userRepository, intent)

    @Provides
    fun provideProfileServiceInteractor(
        serviceRepository: ServiceRepository
    ) =
        ProfileServiceInteractor(serviceRepository)

    @Provides
    fun provideProfileDialogInteractor(
        dialogRepository: DialogRepository
    ) = ProfileDialogInteractor(dialogRepository)

    @Provides
    fun provideProfileSubscriptionInteractor(
        subscriptionRepository: SubscriptionRepository
    ) = ProfileSubscriptionInteractor(subscriptionRepository)

    @Provides
    fun provideCreationServiceServiceServiceInteractor(serviceRepository: ServiceRepository) =
        CreationServiceServiceServiceInteractor(serviceRepository)

    @Provides
    fun provideCreationServiceServiceTagInteractor(tagRepository: TagRepository) =
        CreationServiceTagInteractor(tagRepository)

    @Provides
    fun provideCreationServiceServicePhotoInteractor(photoRepository: PhotoRepository) =
        CreationServicePhotoInteractor(photoRepository)

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
    fun provideMainScreenDataInteractor(figuringServicePoints: FiguringServicePoints) =
        MainScreenDataInteractor(intent, figuringServicePoints)

    @Provides
    fun provideServiceInteractor(photoRepository: PhotoRepository) =
        ServiceInteractor(photoRepository, intent)

    @Provides
    fun provideSearchServiceInteractor(userRepository: UserRepository) =
        SearchServiceInteractor(userRepository)

    @Provides
    fun provideEditProfileInteractor(userRepository: UserRepository) =
        EditProfileInteractor(
            intent, userRepository
        )

    @Provides
    fun provideDialogsDialogInteractor(dialogRepository: DialogRepository) =
        DialogsDialogInteractor(dialogRepository)

    @Provides
    fun provideDialogsUserInteractor(userRepository: UserRepository) =
        DialogsUserInteractor(userRepository)

    @Provides
    fun provideMessagesMessageInteractor(messageRepository: MessageRepository) =
        MessagesMessageInteractor(messageRepository)

    @Provides
    fun provideMessagesDialogInteractor(dialogRepository: DialogRepository) =
        MessagesDialogInteractor(intent, dialogRepository)

    @Provides
    fun provideScheduleInteractor() = ScheduleInteractor()

    @Provides
    fun provideSubscribersInteractor(subscriptionRepository: SubscriptionRepository) =
        SubscriptionsSubscriptionInteractor(subscriptionRepository)

    @Provides
    fun provideMessagesUserInteractor() = MessagesUserInteractor(intent)

    @Provides
    fun provideEditServiceInteractor(serviceRepository: ServiceRepository) =
        EditServiceInteractor(intent, serviceRepository)

    //APIs
    @Provides
    fun provideFigureServicePointsApi() = FiguringServicePoints()

}