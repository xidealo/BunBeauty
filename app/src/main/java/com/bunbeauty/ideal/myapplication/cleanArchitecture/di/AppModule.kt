package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import dagger.Module
import dagger.Provides

@Module
abstract class AppModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideAuthorizationInteractor(): AuthorizationInteractor = AuthorizationInteractor()


        @JvmStatic
        @Provides
        fun provideVerifyPhoneInteractor(): VerifyPhoneInteractor = VerifyPhoneInteractor()
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
