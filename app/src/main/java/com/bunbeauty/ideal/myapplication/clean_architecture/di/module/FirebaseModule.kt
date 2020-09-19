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
   @ActivityScope
    fun provideUserFirebase() = UserFirebase()

    @Provides
   @ActivityScope
    fun provideServiceFirebase() = ServiceFirebase()

    @Provides
   @ActivityScope
    fun provideTagFirebase() = TagFirebase()

    @Provides
   @ActivityScope
    fun providePhotoFirebase() = PhotoServiceFirebase()

    @Provides
   @ActivityScope
    fun provideCodeFirebase() = CodeFirebase()

    @Provides
   @ActivityScope
    fun provideOrderFirebase() = OrderFirebase()

    @Provides
   @ActivityScope
    fun provideDialogFirebase() = DialogFirebase()

    @Provides
   @ActivityScope
    fun provideMessageFirebase() = MessageFirebase()

    @Provides
   @ActivityScope
    fun provideSubscriptionFirebase() = SubscriptionFirebase()

    @Provides
   @ActivityScope
    fun provideSubscriberFirebase() = SubscriberFirebase()

    @Provides
   @ActivityScope
    fun provideUserCommentFirebase() = UserCommentFirebase()

    @Provides
   @ActivityScope
    fun provideServiceCommentFirebase() = ServiceCommentFirebase()

    @Provides
   @ActivityScope
    fun provideScheduleFirebase() = ScheduleFirebase()
}