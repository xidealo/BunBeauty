package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.DialogAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.MessageAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ServiceAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.SubscriptionAdapter
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

}