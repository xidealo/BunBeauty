package com.bunbeauty.ideal.myapplication.clean_architecture.di.module

import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.ProfileOrderAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.ProfileServiceAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.OrdersFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.ServicesFragment
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FragmentModule {

    @Provides
    @Singleton
    fun provideOrdersFragment(
        profileOrderAdapter: ProfileOrderAdapter
    ) = OrdersFragment.newInstance(profileOrderAdapter)

    @Provides
    @Singleton
    fun provideServicesFragment(
        profileServiceAdapter: ProfileServiceAdapter
    ) = ServicesFragment.newInstance(profileServiceAdapter)

}