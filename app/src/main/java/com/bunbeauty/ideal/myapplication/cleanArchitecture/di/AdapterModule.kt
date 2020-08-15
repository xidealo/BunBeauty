package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.*
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

    @Provides
    @Singleton
    fun provideProfileServiceAdapter() = ProfileServiceAdapter()

    @Provides
    @Singleton
    fun provideProfileOrderAdapter() = ProfileOrderAdapter()
}