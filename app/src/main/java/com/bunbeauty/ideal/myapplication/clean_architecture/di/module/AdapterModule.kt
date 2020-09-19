package com.bunbeauty.ideal.myapplication.clean_architecture.di.module

import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.ActivityScope
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.AppScope
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AdapterModule {

    @Provides
   @ActivityScope
    fun provideServiceAdapter() = ServiceAdapter()

    @Provides
   @ActivityScope
    fun provideDialogAdapter() = DialogAdapter()

    @Provides
   @ActivityScope
    fun provideMessageAdapter() = MessageAdapter()

    @Provides
   @ActivityScope
    fun provideSubscriptionAdapter() = SubscriptionAdapter()

    @Provides
   @ActivityScope
    fun providePhotoAdapter() = PhotoAdapter()

    @Provides
   @ActivityScope
    fun provideChangeablePhotoAdapter() = ChangeablePhotoAdapter()

    @Provides
   @ActivityScope
    fun provideUserCommentAdapter() = UserCommentAdapter()

    @Provides
   @ActivityScope
    fun provideServiceCommentAdapter() = ServiceCommentAdapter()

}