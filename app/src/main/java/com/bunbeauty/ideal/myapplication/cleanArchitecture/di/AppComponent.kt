package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.AuthorizationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.AuthorizationActivity
import dagger.Component
import javax.inject.Singleton
import android.app.Application
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.LocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.UserRepo


/*
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(authorizationPresenter: AuthorizationPresenter)
    fun inject(authorizationActivity: AuthorizationActivity)

    */
/*fun productDao(): UserDao

    fun demoDatabase(): LocalDatabase

    fun productRepository(): UserRepo

    fun application(): Application*//*

}*/

@Singleton
@Component(dependencies = [], modules = [AppModule::class])
interface AppComponent {

    fun inject(authorizationActivity: AuthorizationActivity)

    fun inject(authorizationPresenter: AuthorizationPresenter)

}
