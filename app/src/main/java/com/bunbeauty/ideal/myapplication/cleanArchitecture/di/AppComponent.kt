package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.AddingServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments.PremiumElementFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments.SearchServiceFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.AuthorizationActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.RegistrationActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.VerifyPhoneActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.searchService.MainScreenActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.service.ServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.fragments.PremiumElementPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.fragments.SearchServicePresenter
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

    fun inject(addingServiceActivity: AddingServiceActivity)
    fun inject(addingServicePresenter: AddingServicePresenter)

    fun inject(premiumElementFragment: PremiumElementFragment)
    fun inject(premiumElementPresenter: PremiumElementPresenter)

    fun inject(mainScreenActivity: MainScreenActivity)
    fun inject(mainScreenPresenter: MainScreenPresenter)

    fun inject(serviceActivity: ServiceActivity)
    fun inject(servicePresenter: ServicePresenter)

    fun inject(searchServiceFragment: SearchServiceFragment)
    fun inject(searchServicePresenter: SearchServicePresenter)
}
