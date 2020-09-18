package com.bunbeauty.ideal.myapplication.clean_architecture.di.module

import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.*
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.ActivityScope
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.AppScope
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun provideUserFirebase() = UserFirebase()

    @Provides
    @Singleton
    fun provideServiceFirebase() = ServiceFirebase()

    @Provides
    @Singleton
    fun provideTagFirebase() = TagFirebase()

    @Provides
    @Singleton
    fun providePhotoFirebase() = PhotoServiceFirebase()

    @Provides
    @Singleton
    fun provideCodeFirebase() = CodeFirebase()

    @Provides
    @Singleton
    fun provideOrderFirebase() = OrderFirebase()

    @Provides
    @Singleton
    fun provideDialogFirebase() = DialogFirebase()

    @Provides
    @Singleton
    fun provideMessageFirebase() = MessageFirebase()

    @Provides
    @Singleton
    fun provideSubscriptionFirebase() = SubscriptionFirebase()

    @Provides
    @Singleton
    fun provideSubscriberFirebase() = SubscriberFirebase()

    @Provides
    @Singleton
    fun provideUserCommentFirebase() = UserCommentFirebase()

    @Provides
    @Singleton
    fun provideServiceCommentFirebase() = ServiceCommentFirebase()

    @Provides
    @Singleton
    fun provideScheduleFirebase() = ScheduleFirebase()
}