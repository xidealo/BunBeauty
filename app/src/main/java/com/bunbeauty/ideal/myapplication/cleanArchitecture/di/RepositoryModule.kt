package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IScheduleRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao, userFirebase: UserFirebase) =
        UserRepository(userDao, userFirebase)

    @Provides
    @Singleton
    fun provideServiceRepository(serviceDao: ServiceDao, serviceFirebase: ServiceFirebase) =
        ServiceRepository(serviceDao, serviceFirebase)

    @Provides
    @Singleton
    fun provideTagRepository(tagDao: TagDao, tagFirebase: TagFirebase) =
        TagRepository(tagDao, tagFirebase)

    @Provides
    @Singleton
    fun providePhotoRepository(photoDao: PhotoDao, photoFirebase: PhotoFirebase) =
        PhotoRepository(photoDao, photoFirebase)

    @Provides
    @Singleton
    fun provideCodeRepository(codeDao: CodeDao, codeFirebase: CodeFirebase) =
        CodeRepository(codeDao, codeFirebase)

    @Provides
    @Singleton
    fun provideDialogRepository(dialogDao: DialogDao, dialogFirebase: DialogFirebase) =
        DialogRepository(dialogDao, dialogFirebase)

    @Provides
    @Singleton
    fun provideMessageRepository(messageFirebase: MessageFirebase) =
        MessageRepository(messageFirebase)

    @Provides
    @Singleton
    fun provideOrderRepository(orderFirebase: OrderFirebase) =
        OrderRepository(orderFirebase)

    @Provides
    @Singleton
    fun provideSubscriptionRepository(subscriptionFirebase: SubscriptionFirebase) =
        SubscriptionRepository(subscriptionFirebase)

    @Provides
    @Singleton
    fun provideSubscriberRepository(subscriberFirebase: SubscriberFirebase) =
        SubscriberRepository(subscriberFirebase)

    @Provides
    @Singleton
    fun provideUserCommentRepository(userCommentFirebase: UserCommentFirebase) =
        UserCommentRepository(userCommentFirebase)

    @Provides
    @Singleton
    fun provideServiceCommentRepository(serviceCommentFirebase: ServiceCommentFirebase) =
        ServiceCommentRepository(serviceCommentFirebase)

    @Provides
    @Singleton
    fun provideScheduleRepository(scheduleFirebase: ScheduleFirebase): IScheduleRepository =
        ScheduleRepository(scheduleFirebase)

}