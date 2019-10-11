package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.AuthorizationActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(dependencies = [], modules = [RoomModule::class])
interface RoomComponent {

    fun inject(authorizationActivity: AuthorizationActivity)

}