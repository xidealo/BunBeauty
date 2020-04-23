package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.EditProfileView


@InjectViewState
class EditProfilePresenter(private val editProfileInteractor: EditProfileInteractor):MvpPresenter<EditProfileView>() {
}