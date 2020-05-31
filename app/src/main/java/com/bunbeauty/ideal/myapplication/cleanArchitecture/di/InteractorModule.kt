package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.FiguringServicePoints
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.api.VerifyPhoneNumberApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.CreationCommentMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.CreationCommentOrderInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.CreationCommentServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.CreationCommentUserCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.currentComment.CurrentCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.ServiceCommentsServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.ServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments.UserCommentsUserCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments.UserCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.SearchServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementCodeInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule.ScheduleInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.EditServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.SubscriptionsSubscriberInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.SubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.SubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.*
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
    fun provideRegistrationInteractor(userRepository: UserRepository) =
        RegistrationUserInteractor(userRepository, intent)

    @Provides
    @Singleton
    fun provideProfileUserInteractor(
        userRepository: UserRepository
    ) =
        ProfileUserInteractor(userRepository, intent)

    @Provides
    @Singleton
    fun provideProfileServiceInteractor(
        serviceRepository: ServiceRepository
    ) =
        ProfileServiceInteractor(serviceRepository)

    @Provides
    @Singleton
    fun provideProfileDialogInteractor(
        dialogRepository: DialogRepository
    ) = ProfileDialogInteractor(dialogRepository)

    @Provides
    @Singleton
    fun provideProfileSubscriptionInteractor(
        subscriptionRepository: SubscriptionRepository
    ) = ProfileSubscriptionInteractor(subscriptionRepository)

    @Provides
    @Singleton
    fun provideProfileSubscriberInteractor(
        subscriberRepository: SubscriberRepository
    ) = ProfileSubscriberInteractor(subscriberRepository)

    @Provides
    @Singleton
    fun provideCreationServiceServiceServiceInteractor(serviceRepository: ServiceRepository) =
        CreationServiceServiceServiceInteractor(serviceRepository)

    @Provides
    @Singleton
    fun provideCreationServiceServiceTagInteractor(tagRepository: TagRepository) =
        CreationServiceTagInteractor(tagRepository)

    @Provides
    @Singleton
    fun provideCreationServiceServicePhotoInteractor(photoRepository: PhotoRepository) =
        CreationServicePhotoInteractor(photoRepository)

    @Provides
    @Singleton
    fun providePremiumElementCodeInteractor(codeRepository: CodeRepository) =
        PremiumElementCodeInteractor(
            codeRepository
        )

    @Provides
    @Singleton
    fun providePremiumElementServiceInteractor(serviceRepository: ServiceRepository) =
        PremiumElementServiceInteractor(
            serviceRepository
        )

    @Provides
    @Singleton
    fun provideMainScreenUserInteractor(
        userRepository: UserRepository
    ) =
        MainScreenUserInteractor(userRepository)

    @Provides
    @Singleton
    fun provideMainScreenServiceInteractor(
        serviceRepository: ServiceRepository
    ) =
        MainScreenServiceInteractor(serviceRepository)

    @Provides
    @Singleton
    fun provideMainScreenDataInteractor(figuringServicePoints: FiguringServicePoints) =
        MainScreenDataInteractor(intent, figuringServicePoints)

    @Provides
    @Singleton
    fun provideServiceInteractor(photoRepository: PhotoRepository) =
        ServiceInteractor(photoRepository, intent)

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
    fun provideScheduleInteractor() = ScheduleInteractor()

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
    fun provideEditServiceInteractor(serviceRepository: ServiceRepository) =
        EditServiceInteractor(intent, serviceRepository)

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
        CreationCommentUserCommentInteractor(
            userCommentRepository,
            intent
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
        CreationCommentMessageInteractor(
            messageRepository,
            intent
        )

    @Provides
    @Singleton
    fun provideCreationCommentOrderInteractor(orderRepository: OrderRepository) =
        CreationCommentOrderInteractor(
            orderRepository
        )
}