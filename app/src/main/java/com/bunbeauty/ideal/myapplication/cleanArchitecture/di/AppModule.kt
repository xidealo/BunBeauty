package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.AuthorizationPresenter
import dagger.Module
import dagger.Provides
import android.app.Application
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.LocalDatabase
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideAuthorizationInteractor(): AuthorizationInteractor = AuthorizationInteractor()

        @JvmStatic
        @Provides
        fun provideAuthorizationPresenter(): AuthorizationPresenter = AuthorizationPresenter()

    }
}

/*@Module
class AppModule() {

    @Provides
    @Singleton
    internal fun providesApplication(): Application {
        return app
    }
    @Provides
    @Singleton
    internal fun provideAuthorizationInteractor() : AuthorizationInteractor = AuthorizationInteractor()

    @Provides
    @Singleton
    internal fun provideAuthorizationPresenter() : AuthorizationPresenter = AuthorizationPresenter()

}*/
