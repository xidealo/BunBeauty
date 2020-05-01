package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.ScheduleActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat.DialogsActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.CreationServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing.EditProfileActivity
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
    fun inject(verifyPhoneActivity: VerifyPhoneActivity)
    fun inject(registrationActivity: RegistrationActivity)
    fun inject(profileActivity: ProfileActivity)
    fun inject(creationServiceActivity: CreationServiceActivity)
    fun inject(premiumElementFragment: PremiumElementFragment)
    fun inject(mainScreenActivity: MainScreenActivity)
    fun inject(serviceActivity: ServiceActivity)
    fun inject(searchServiceFragment: SearchServiceFragment)
    fun inject(editProfileActivity: EditProfileActivity)
    fun inject(scheduleActivity: ScheduleActivity)
    fun inject(dialogsActivity: DialogsActivity)
}
