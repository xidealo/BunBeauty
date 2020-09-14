package com.bunbeauty.ideal.myapplication.clean_architecture.di.module

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.FiguringServicePointsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.VerifyPhoneNumberApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.dialog.DialogsDialogInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.dialog.DialogsMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.dialog.DialogsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.MessagesDialogInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.MessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.MessagesOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.MessagesUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.i_message.MessagesScheduleInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.creation_comment.*
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.current_comment.CurrentCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.ServiceCommentsServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.ServiceCommentsServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.ServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.user_comments.UserCommentsUserCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.user_comments.UserCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.create_service.CreationServiceServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.create_service.CreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.editing.profile.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.editing.service.EditServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.editing.service.EditServiceTagInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.fragments.SearchServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.premium.PremiumFragmentCodeInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.premium.PremiumFragmentServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.RegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.iLogIn.IRegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.photo.PhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile.*
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile.iProfile.*
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.schedule.ScheduleInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service.MainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service.MainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.service.ServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.service.ServiceUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.SessionsInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.SessionsMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.SessionsOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.subs.SubscriptionsSubscriberInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.subs.SubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.subs.SubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IScheduleRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository
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
    fun provideProfileOrderInteractor(orderRepository: OrderRepository): IProfileOrderInteractor =
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
        PremiumFragmentCodeInteractor(
            codeRepository
        )

    @Provides
    @Singleton
    fun providePremiumElementServiceInteractor(serviceRepository: IServiceRepository) =
        PremiumFragmentServiceInteractor(serviceRepository)

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
    fun provideMessagesUserInteractor() = MessagesUserInteractor(intent)

    @Provides
    @Singleton
    fun provideMessagesOrderInteractor(orderRepository: OrderRepository) =
        MessagesOrderInteractor(orderRepository)

    @Provides
    @Singleton
    fun provideMessagesScheduleInteractor(scheduleRepository: IScheduleRepository) =
        MessagesScheduleInteractor(scheduleRepository)

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
    fun provideCreationCommentOrderInteractor(orderRepository: OrderRepository) =
        CreationCommentOrderInteractor(orderRepository)

    @Provides
    @Singleton
    fun provideCreationCommentServiceInteractor(serviceRepository: IServiceRepository) =
        CreationCommentServiceInteractor(serviceRepository)

    @Provides
    @Singleton
    fun provideServiceCommentsServiceInteractor() = ServiceCommentsServiceInteractor(intent)

    @Provides
    @Singleton
    fun provideSessionsInteractor(scheduleRepository: IScheduleRepository) =
        SessionsInteractor(scheduleRepository, intent)

    @Provides
    @Singleton
    fun provideSessionsOrderInteractor(orderRepository: OrderRepository) =
        SessionsOrderInteractor(orderRepository)

    @Provides
    @Singleton
    fun provideSessionsMessageInteractor(messageRepository: MessageRepository) =
        SessionsMessageInteractor(messageRepository)

}