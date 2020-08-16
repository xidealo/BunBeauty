package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.api.FiguringServicePointsApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.api.VerifyPhoneNumberApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.currentComment.CurrentCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.ServiceCommentsServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.ServiceCommentsServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.ServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments.UserCommentsUserCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments.UserCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.profile.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.service.EditServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.service.EditServiceTagInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.SearchServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementCodeInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IRegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo.PhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule.ScheduleInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions.SessionsInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions.SessionsOrderInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.SubscriptionsSubscriberInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.SubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.SubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IOrderRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IScheduleRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IServiceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class InteractorModule(private val intent: Intent) {

    @Provides
    @Singleton
    fun provideAuthorizationInteractor(userRepository: UserRepository) =
        AuthorizationInteractor(userRepository)

    @Provides
    @Singleton
    fun provideVerifyPhoneInteractor(
        userRepository: UserRepository,
        verifyPhoneNumberApi: VerifyPhoneNumberApi
    ) =
        VerifyPhoneInteractor(userRepository, intent, verifyPhoneNumberApi)

    @Provides
    @Singleton
    fun provideRegistrationInteractor(userRepository: UserRepository): IRegistrationUserInteractor =
        RegistrationUserInteractor(userRepository, intent)

    @Provides
    @Singleton
    fun provideProfileUserInteractor(userRepository: UserRepository): IProfileUserInteractor =
        ProfileUserInteractor(userRepository, intent)

    @Provides
    @Singleton
    fun provideProfileServiceInteractor(serviceRepository: IServiceRepository): IProfileServiceInteractor =
        ProfileServiceInteractor(serviceRepository)

    @Provides
    @Singleton
    fun provideProfileOrderInteractor(orderRepository: IOrderRepository): IProfileOrderInteractor =
        ProfileOrderInteractor(orderRepository)

    @Provides
    @Singleton
    fun provideProfileDialogInteractor(dialogRepository: DialogRepository): IProfileDialogInteractor =
        ProfileDialogInteractor(dialogRepository)

    @Provides
    @Singleton
    fun provideProfileSubscriptionInteractor(subscriptionRepository: SubscriptionRepository): IProfileSubscriptionInteractor =
        ProfileSubscriptionInteractor(subscriptionRepository)

    @Provides
    @Singleton
    fun provideProfileSubscriberInteractor(subscriberRepository: SubscriberRepository): IProfileSubscriberInteractor =
        ProfileSubscriberInteractor(subscriberRepository)

    @Provides
    @Singleton
    fun provideCreationServiceServiceServiceInteractor(serviceRepository: IServiceRepository) =
        CreationServiceServiceServiceInteractor(serviceRepository)

    @Provides
    @Singleton
    fun provideCreationServiceServiceTagInteractor(tagRepository: TagRepository) =
        CreationServiceTagInteractor(tagRepository)

    @Provides
    @Singleton
    fun provideCreationServiceServicePhotoInteractor(photoServiceRepository: PhotoServiceRepository) =
        PhotoInteractor(
            photoServiceRepository
        )

    @Provides
    @Singleton
    fun providePremiumElementCodeInteractor(codeRepository: CodeRepository) =
        PremiumElementCodeInteractor(
            codeRepository
        )

    @Provides
    @Singleton
    fun providePremiumElementServiceInteractor(serviceRepository: IServiceRepository) =
        PremiumElementServiceInteractor(serviceRepository)

    @Provides
    @Singleton
    fun provideMainScreenUserInteractor(
        userRepository: UserRepository
    ) =
        MainScreenUserInteractor(userRepository)

    @Provides
    @Singleton
    fun provideMainScreenServiceInteractor(serviceRepository: IServiceRepository) =
        MainScreenServiceInteractor(serviceRepository)

    @Provides
    @Singleton
    fun provideMainScreenDataInteractor(figuringServicePointsApi: FiguringServicePointsApi) =
        MainScreenDataInteractor(intent, figuringServicePointsApi)

    @Provides
    @Singleton
    fun provideServiceServiceInteractor(serviceRepository: IServiceRepository) =
        ServiceInteractor(serviceRepository, intent)

    @Provides
    @Singleton
    fun provideServiceUserInteractor(userRepository: UserRepository) =
        ServiceUserInteractor(userRepository, intent)

    @Provides
    @Singleton
    fun provideServicePhotoInteractor(photoServiceRepository: PhotoServiceRepository) =
        ServicePhotoInteractor(photoServiceRepository)

    @Provides
    @Singleton
    fun provideSearchServiceInteractor(userRepository: UserRepository) =
        SearchServiceInteractor(userRepository)

    @Provides
    @Singleton
    fun provideEditProfileInteractor(
        userRepository: UserRepository,
        verifyPhoneNumberApi: VerifyPhoneNumberApi
    ) = EditProfileInteractor(intent, userRepository, verifyPhoneNumberApi)

    @Provides
    @Singleton
    fun provideDialogsDialogInteractor(dialogRepository: DialogRepository) =
        DialogsDialogInteractor(dialogRepository)

    @Provides
    @Singleton
    fun provideDialogsUserInteractor(userRepository: UserRepository) =
        DialogsUserInteractor(userRepository)

    @Provides
    @Singleton
    fun provideDialogsMessageInteractor(messageRepository: MessageRepository) =
        DialogsMessageInteractor(messageRepository)

    @Provides
    @Singleton
    fun provideMessagesMessageInteractor(messageRepository: MessageRepository) =
        MessagesMessageInteractor(messageRepository)

    @Provides
    @Singleton
    fun provideMessagesDialogInteractor(dialogRepository: DialogRepository) =
        MessagesDialogInteractor(intent, dialogRepository)

    @Provides
    @Singleton
    fun provideScheduleInteractor(scheduleRepository: IScheduleRepository) =
        ScheduleInteractor(scheduleRepository)

    @Provides
    @Singleton
    fun provideSubscriptionsSubscriptionInteractor(subscriptionRepository: SubscriptionRepository) =
        SubscriptionsSubscriptionInteractor(subscriptionRepository)

    @Provides
    @Singleton
    fun provideSubscriptionsUserInteractor(userRepository: UserRepository) =
        SubscriptionsUserInteractor(intent, userRepository)

    @Provides
    @Singleton
    fun provideSubscriptionsSubscriberInteractor(subscriberRepository: SubscriberRepository) =
        SubscriptionsSubscriberInteractor(subscriberRepository)

    @Provides
    @Singleton
    fun provideMessagesUserInteractor() = MessagesUserInteractor(intent)

    @Provides
    @Singleton
    fun provideEditServiceInteractor(serviceRepository: IServiceRepository) =
        EditServiceServiceInteractor(intent, serviceRepository)

    @Provides
    @Singleton
    fun provideEditServiceTagInteractor(tagRepository: TagRepository) =
        EditServiceTagInteractor(tagRepository)

    @Provides
    @Singleton
    fun provideUserCommentsUserCommentInteractor(userCommentRepository: UserCommentRepository) =
        UserCommentsUserCommentInteractor(
            userCommentRepository
        )

    @Provides
    @Singleton
    fun provideUserCommentsUserInteractor(userRepository: UserRepository) =
        UserCommentsUserInteractor(
            userRepository, intent
        )

    @Provides
    @Singleton
    fun provideServiceCommentsServiceCommentInteractor(serviceCommentRepository: ServiceCommentRepository) =
        ServiceCommentsServiceCommentInteractor(serviceCommentRepository)

    @Provides
    @Singleton
    fun provideServiceCommentsUserInteractor(userRepository: UserRepository) =
        ServiceCommentsUserInteractor(userRepository)

    @Provides
    @Singleton
    fun provideCurrentCommentInteractor() =
        CurrentCommentCommentInteractor(
            intent
        )

    @Provides
    @Singleton
    fun provideCreationCommentUserCommentInteractor(userCommentRepository: UserCommentRepository) =
        CreationCommentUserCommentInteractor(userCommentRepository)

    @Provides
    @Singleton
    fun provideCreationCommentUserInteractor(userRepository: UserRepository) =
        CreationCommentUserInteractor(
            intent,
            userRepository
        )

    @Provides
    @Singleton
    fun provideCreationCommentServiceCommentInteractor(serviceCommentRepository: ServiceCommentRepository) =
        CreationCommentServiceCommentInteractor(
            serviceCommentRepository
        )

    @Provides
    @Singleton
    fun provideCreationCommentMessageInteractor(messageRepository: MessageRepository) =
        CreationCommentMessageInteractor(messageRepository, intent)

    @Provides
    @Singleton
    fun provideCreationCommentOrderInteractor(orderRepository: IOrderRepository) =
        CreationCommentOrderInteractor(orderRepository)

    @Provides
    @Singleton
    fun provideServiceCommentsServiceInteractor() = ServiceCommentsServiceInteractor(intent)

    @Provides
    @Singleton
    fun provideSessionsInteractor(scheduleRepository: IScheduleRepository) =
        SessionsInteractor(scheduleRepository, intent)

    @Provides
    @Singleton
    fun provideSessionsOrderInteractor(orderRepository: IOrderRepository) =
        SessionsOrderInteractor(orderRepository)
}