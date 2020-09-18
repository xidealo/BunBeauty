package com.bunbeauty.ideal.myapplication.clean_architecture.di.module

import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.FragmentScope
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.ProfileOrderAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.ProfileServiceAdapter
import dagger.Module
import dagger.Provides

@Module
class FragmentModule {
    @Provides
    @FragmentScope
    fun provideProfileServiceAdapter() = ProfileServiceAdapter()

    @Provides
    @FragmentScope
    fun provideProfileOrderAdapter() = ProfileOrderAdapter()
}