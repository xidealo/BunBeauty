package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.AuthorizationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.AuthorizationActivity
import dagger.Component



@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(authorizationPresenter: AuthorizationPresenter)
    fun inject(authorizationActivity: AuthorizationActivity)
}