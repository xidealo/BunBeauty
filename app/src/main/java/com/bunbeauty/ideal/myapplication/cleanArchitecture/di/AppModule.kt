package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import dagger.Module
import dagger.Provides

@Module
abstract class AppModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideAuthorizationInteractor(): AuthorizationInteractor = AuthorizationInteractor()

        /*@JvmStatic
        @Provides
        fun provideAuthorizationPresenter(authorizationInteractor: AuthorizationInteractor): AuthorizationPresenter = AuthorizationPresenter(authorizationInteractor)
*/
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
