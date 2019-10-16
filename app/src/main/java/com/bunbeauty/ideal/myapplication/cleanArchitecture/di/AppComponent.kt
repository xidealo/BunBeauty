package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.AuthorizationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.RegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.VerifyPhonePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.AuthorizationActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.RegistrationActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.VerifyPhoneActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.profile.ProfileActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(dependencies = [], modules = [AppModule::class])
interface AppComponent {

    fun inject(authorizationActivity: AuthorizationActivity)
    fun inject(authorizationPresenter: AuthorizationPresenter)

    fun inject(verifyPhoneActivity: VerifyPhoneActivity)
    fun inject(verifyPhonePresenter: VerifyPhonePresenter)

    fun inject(registrationActivity: RegistrationActivity)
    fun inject(registrationPresenter: RegistrationPresenter)

    fun inject(profileActivity: ProfileActivity)
    fun inject(profilePresenter: ProfilePresenter)
}
