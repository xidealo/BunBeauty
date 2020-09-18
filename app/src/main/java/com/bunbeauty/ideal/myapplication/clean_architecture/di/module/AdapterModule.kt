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
    @Singleton
    fun provideServiceAdapter() = ServiceAdapter()

    @Provides
    @Singleton
    fun provideDialogAdapter() = DialogAdapter()

    @Provides
    @Singleton
    fun provideMessageAdapter() = MessageAdapter()

    @Provides
    @Singleton
    fun provideSubscriptionAdapter() = SubscriptionAdapter()

    @Provides
    @Singleton
    fun providePhotoAdapter() = PhotoAdapter()

    @Provides
    @Singleton
    fun provideChangeablePhotoAdapter() = ChangeablePhotoAdapter()

    @Provides
    @Singleton
    fun provideUserCommentAdapter() = UserCommentAdapter()

    @Provides
    @Singleton
    fun provideServiceCommentAdapter() = ServiceCommentAdapter()

}