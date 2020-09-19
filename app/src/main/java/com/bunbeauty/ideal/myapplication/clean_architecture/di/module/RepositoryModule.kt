package com.bunbeauty.ideal.myapplication.clean_architecture.di.module

import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dao.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IOrderRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IScheduleRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.ActivityScope
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.AppScope
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @ActivityScope
    fun provideUserRepository(userFirebase: UserFirebase) =
        UserRepository(userFirebase)

    @Provides
    @ActivityScope
    fun provideServiceRepository(
        serviceFirebase: ServiceFirebase
    ): IServiceRepository = ServiceRepository(serviceFirebase)

    @Provides
    @ActivityScope
    fun provideTagRepository(tagFirebase: TagFirebase) =
        TagRepository(tagFirebase)

    @Provides
    @ActivityScope
    fun providePhotoRepository(photoServiceFirebase: PhotoServiceFirebase) =
        PhotoServiceRepository(photoServiceFirebase)

    @Provides
    @ActivityScope
    fun provideCodeRepository(codeFirebase: CodeFirebase) =
        CodeRepository(codeFirebase)

    @Provides
    @ActivityScope
    fun provideDialogRepository(dialogFirebase: DialogFirebase) =
        DialogRepository(dialogFirebase)

    @Provides
    @ActivityScope
    fun provideMessageRepository(messageFirebase: MessageFirebase) =
        MessageRepository(messageFirebase)

    @Provides
    @ActivityScope
    fun provideOrderRepository(orderFirebase: OrderFirebase) = OrderRepository(orderFirebase)

    @Provides
    @ActivityScope
    fun provideSubscriptionRepository(subscriptionFirebase: SubscriptionFirebase) =
        SubscriptionRepository(subscriptionFirebase)

    @Provides
    @ActivityScope
    fun provideSubscriberRepository(subscriberFirebase: SubscriberFirebase) =
        SubscriberRepository(subscriberFirebase)

    @Provides
    @ActivityScope
    fun provideUserCommentRepository(userCommentFirebase: UserCommentFirebase) =
        UserCommentRepository(userCommentFirebase)

    @Provides
    @ActivityScope
    fun provideServiceCommentRepository(serviceCommentFirebase: ServiceCommentFirebase) =
        ServiceCommentRepository(serviceCommentFirebase)

    @Provides
    @ActivityScope
    fun provideScheduleRepository(scheduleFirebase: ScheduleFirebase): IScheduleRepository =
        ScheduleRepository(scheduleFirebase)

}