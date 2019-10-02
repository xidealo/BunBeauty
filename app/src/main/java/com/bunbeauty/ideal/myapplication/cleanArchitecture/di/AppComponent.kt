package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.content.Context
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.database.LocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.AuthorizationActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun plusAppComponent(module: AppModule): AppComponent

    fun getDatabse(context: Context, name: String): LocalDatabase

    fun inject(authorizationActivity: AuthorizationActivity)

}