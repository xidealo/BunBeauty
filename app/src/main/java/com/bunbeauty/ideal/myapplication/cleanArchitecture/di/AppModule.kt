package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.AuthorizationPresenter
import dagger.Module
import dagger.Provides

@Module
abstract class AppModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideAuthorizationInteractor() : AuthorizationInteractor = AuthorizationInteractor()

        @JvmStatic
        @Provides
        fun provideAuthorizationPresenter() : AuthorizationPresenter = AuthorizationPresenter()

    }
}