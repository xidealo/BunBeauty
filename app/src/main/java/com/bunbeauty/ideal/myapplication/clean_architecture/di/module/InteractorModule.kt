package com.bunbeauty.ideal.myapplication.clean_architecture.di.module

import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IScheduleRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.ActivityScope
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
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.RegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.iLogIn.IRegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.photo.PhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.premium.PremiumFragmentCodeInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.premium.PremiumFragmentServiceInteractor
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
import dagger.Module
import dagger.Provides

@Module
class InteractorModule {

    @Provides
    @ActivityScope
    fun provideAuthorizationInteractor(userRepository: UserRepository) =
        AuthorizationInteractor(userRepository)

    @Provides
    @ActivityScope
    fun provideVerifyPhoneInteractor(
        userRepository: UserRepository,
        verifyPhoneNumberApi: VerifyPhoneNumberApi
    ) =
        VerifyPhoneInteractor(userRepository, verifyPhoneNumberApi)

    @Provides
    @ActivityScope
    fun provideRegistrationInteractor(userRepository: UserRepository): IRegistrationUserInteractor =
        RegistrationUserInteractor(userRepository)

    @Provides
    @ActivityScope
    fun provideProfileUserInteractor(userRepository: UserRepository): IProfileUserInteractor =
        ProfileUserInteractor(userRepository)

    @Provides
    @ActivityScope
    fun provideProfileServiceInteractor(serviceRepository: IServiceRepository): IProfileServiceInteractor =
        ProfileServiceInteractor(serviceRepository)

    @Provides
    @ActivityScope
    fun provideProfileOrderInteractor(orderRepository: OrderRepository): IProfileOrderInteractor =
        ProfileOrderInteractor(orderRepository)

    @Provides
    @ActivityScope
    fun provideProfileDialogInteractor(dialogRepository: DialogRepository): IProfileDialogInteractor =
        ProfileDialogInteractor(dialogRepository)

    @Provides
    @ActivityScope
    fun provideProfileSubscriptionInteractor(subscriptionRepository: SubscriptionRepository): IProfileSubscriptionInteractor =
        ProfileSubscriptionInteractor(subscriptionRepository)

    @Provides
    @ActivityScope
    fun provideProfileSubscriberInteractor(subscriberRepository: SubscriberRepository): IProfileSubscriberInteractor =
        ProfileSubscriberInteractor(subscriberRepository)

    @Provides
    @ActivityScope
    fun provideCreationServiceServiceServiceInteractor(serviceRepository: IServiceRepository) =
        CreationServiceServiceServiceInteractor(serviceRepository)

    @Provides
    @ActivityScope
    fun provideCreationServiceServiceTagInteractor(tagRepository: TagRepository) =
        CreationServiceTagInteractor(tagRepository)

    @Provides
    @ActivityScope
    fun provideCreationServiceServicePhotoInteractor(photoServiceRepository: PhotoServiceRepository) =
        PhotoInteractor(
            photoServiceRepository
        )

    @Provides
    @ActivityScope
    fun providePremiumElementCodeInteractor(codeRepository: CodeRepository) =
        PremiumFragmentCodeInteractor(
            codeRepository
        )

    @Provides
    @ActivityScope
    fun providePremiumElementServiceInteractor(serviceRepository: IServiceRepository) =
        PremiumFragmentServiceInteractor(serviceRepository)

    @Provides
    @ActivityScope
    fun provideMainScreenUserInteractor(
        userRepository: UserRepository
    ) =
        MainScreenUserInteractor(userRepository)

    @Provides
    @ActivityScope
    fun provideMainScreenServiceInteractor(serviceRepository: IServiceRepository) =
        MainScreenServiceInteractor(serviceRepository)

    @Provides
    @ActivityScope
    fun provideMainScreenDataInteractor(figuringServicePointsApi: FiguringServicePointsApi) =
        MainScreenDataInteractor(figuringServicePointsApi)

    @Provides
    @ActivityScope
    fun provideServiceServiceInteractor(serviceRepository: IServiceRepository) =
        ServiceInteractor(serviceRepository)

    @Provides
    @ActivityScope
    fun provideServiceUserInteractor(userRepository: UserRepository) =
        ServiceUserInteractor(userRepository)

    @Provides
    @ActivityScope
    fun provideServicePhotoInteractor(photoServiceRepository: PhotoServiceRepository) =
        ServicePhotoInteractor(photoServiceRepository)

    @Provides
    @ActivityScope
    fun provideEditProfileInteractor(
        userRepository: UserRepository,
        verifyPhoneNumberApi: VerifyPhoneNumberApi
    ) = EditProfileInteractor(userRepository, verifyPhoneNumberApi)

    @Provides
    @ActivityScope
    fun provideDialogsDialogInteractor(dialogRepository: DialogRepository) =
        DialogsDialogInteractor(dialogRepository)

    @Provides
    @ActivityScope
    fun provideDialogsUserInteractor(userRepository: UserRepository) =
        DialogsUserInteractor(userRepository)

    @Provides
    @ActivityScope
    fun provideDialogsMessageInteractor(messageRepository: MessageRepository) =
        DialogsMessageInteractor(messageRepository)

    @Provides
    @ActivityScope
    fun provideMessagesMessageInteractor(messageRepository: MessageRepository) =
        MessagesMessageInteractor(messageRepository)

    @Provides
    @ActivityScope
    fun provideMessagesDialogInteractor(dialogRepository: DialogRepository) =
        MessagesDialogInteractor(dialogRepository)

    @Provides
    @ActivityScope
    fun provideMessagesUserInteractor() = MessagesUserInteractor()

    @Provides
    @ActivityScope
    fun provideMessagesOrderInteractor(orderRepository: OrderRepository) =
        MessagesOrderInteractor(orderRepository)

    @Provides
    @ActivityScope
    fun provideMessagesScheduleInteractor(scheduleRepository: IScheduleRepository) =
        MessagesScheduleInteractor(scheduleRepository)

    @Provides
    @ActivityScope
    fun provideScheduleInteractor(scheduleRepository: IScheduleRepository) =
        ScheduleInteractor(scheduleRepository)

    @Provides
    @ActivityScope
    fun provideSubscriptionsSubscriptionInteractor(subscriptionRepository: SubscriptionRepository) =
        SubscriptionsSubscriptionInteractor(subscriptionRepository)

    @Provides
    @ActivityScope
    fun provideSubscriptionsUserInteractor(userRepository: UserRepository) =
        SubscriptionsUserInteractor(userRepository)

    @Provides
    @ActivityScope
    fun provideSubscriptionsSubscriberInteractor(subscriberRepository: SubscriberRepository) =
        SubscriptionsSubscriberInteractor(subscriberRepository)

    @Provides
    @ActivityScope
    fun provideEditServiceInteractor(serviceRepository: IServiceRepository) =
        EditServiceServiceInteractor(serviceRepository)

    @Provides
    @ActivityScope
    fun provideEditServiceTagInteractor(tagRepository: TagRepository) =
        EditServiceTagInteractor(tagRepository)

    @Provides
    @ActivityScope
    fun provideUserCommentsUserCommentInteractor(userCommentRepository: UserCommentRepository) =
        UserCommentsUserCommentInteractor(
            userCommentRepository
        )

    @Provides
    @ActivityScope
    fun provideUserCommentsUserInteractor(userRepository: UserRepository) =
        UserCommentsUserInteractor(
            userRepository
        )

    @Provides
    @ActivityScope
    fun provideServiceCommentsServiceCommentInteractor(serviceCommentRepository: ServiceCommentRepository) =
        ServiceCommentsServiceCommentInteractor(serviceCommentRepository)

    @Provides
    @ActivityScope
    fun provideServiceCommentsUserInteractor(userRepository: UserRepository) =
        ServiceCommentsUserInteractor(userRepository)

    @Provides
    @ActivityScope
    fun provideCurrentCommentInteractor() =
        CurrentCommentCommentInteractor()

    @Provides
    @ActivityScope
    fun provideCreationCommentUserCommentInteractor(userCommentRepository: UserCommentRepository) =
        CreationCommentUserCommentInteractor(userCommentRepository)

    @Provides
    @ActivityScope
    fun provideCreationCommentUserInteractor(userRepository: UserRepository) =
        CreationCommentUserInteractor(userRepository)

    @Provides
    @ActivityScope
    fun provideCreationCommentServiceCommentInteractor(serviceCommentRepository: ServiceCommentRepository) =
        CreationCommentServiceCommentInteractor(
            serviceCommentRepository
        )

    @Provides
    @ActivityScope
    fun provideCreationCommentMessageInteractor(messageRepository: MessageRepository) =
        CreationCommentMessageInteractor(messageRepository)

    @Provides
    @ActivityScope
    fun provideCreationCommentOrderInteractor(orderRepository: OrderRepository) =
        CreationCommentOrderInteractor(orderRepository)

    @Provides
    @ActivityScope
    fun provideCreationCommentServiceInteractor(serviceRepository: IServiceRepository) =
        CreationCommentServiceInteractor(serviceRepository)

    @Provides
    @ActivityScope
    fun provideServiceCommentsServiceInteractor() = ServiceCommentsServiceInteractor()

    @Provides
    @ActivityScope
    fun provideSessionsInteractor(scheduleRepository: IScheduleRepository) =
        SessionsInteractor(scheduleRepository)

    @Provides
    @ActivityScope
    fun provideSessionsOrderInteractor(orderRepository: OrderRepository) =
        SessionsOrderInteractor(orderRepository)

    @Provides
    @ActivityScope
    fun provideSessionsMessageInteractor(messageRepository: MessageRepository) =
        SessionsMessageInteractor(messageRepository)

}