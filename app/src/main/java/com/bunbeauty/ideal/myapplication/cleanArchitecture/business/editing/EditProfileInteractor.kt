package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.IRegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.EditProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule_ProvideUserRepositoryFactory
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.EditProfilePresenter

class EditProfileInteractor(
    private val intent: Intent,
    private val userRepository: UserRepository
) {
    private lateinit var editProfilePresenterCallback: EditProfilePresenterCallback
    fun createEditProfileScreen(editProfilePresenterCallback: EditProfilePresenterCallback) {
        val user = intent.getSerializableExtra(User.USER) as User
        editProfilePresenterCallback.showEditProfile(user)
    }

    fun saveData(
        user: User,
        editProfilePresenterCallback: EditProfilePresenterCallback
    ) {
        this.editProfilePresenterCallback = editProfilePresenterCallback
        if (isNameCorrect(user.name, editProfilePresenterCallback)
            && isSurnameCorrect(user.surname, editProfilePresenterCallback)
            && isCityCorrect(user.city, editProfilePresenterCallback)
        ) {
        }
    }

    fun getIsCityInputCorrect(city: String): Boolean {
        if (city == "Выбрать город") {
            return false
        }
        return true
    }

    fun getIsSurnameInputCorrect(surname: String): Boolean {
        if (!surname.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    fun getIsSurnameLengthLessTwenty(surname: String): Boolean {
        if (surname.length > 20) {
            return false
        }
        return true
    }

    fun getIsNameInputCorrect(name: String): Boolean {
        if (!name.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    fun getIsNameLengthLessTwenty(name: String): Boolean {
        if (name.length > 20) {
            return false
        }
        return true
    }

    private fun isNameCorrect(
        name: String,
        editProfilePresenterCallback: EditProfilePresenterCallback
    ): Boolean {
        if (name.isEmpty()) {
            editProfilePresenterCallback.nameEditProfileInputErrorEmpty()
            return false
        }

        if (!getIsNameInputCorrect(name)) {
            editProfilePresenterCallback.nameEditProfileInputError()
            return false
        }

        if (!getIsNameLengthLessTwenty(name)) {
            editProfilePresenterCallback.nameEditProfileInputErrorLong()
            return false
        }
        return true
    }

    private fun isSurnameCorrect(
        surname: String,
        editProfilePresenterCallback: EditProfilePresenterCallback
    ): Boolean {
        if (surname.isEmpty()) {
            editProfilePresenterCallback.surnameEditProfileInputErrorEmpty()
            return false
        }

        if (!getIsSurnameInputCorrect(surname)) {
            editProfilePresenterCallback.surnameEditProfileInputError()
            return false
        }

        if (!getIsSurnameLengthLessTwenty(surname)) {
            editProfilePresenterCallback.surnameEditProfileEditErrorLong()
            return false
        }
        return true
    }

    private fun isCityCorrect(
        city: String,
        editProfilePresenterCallback: EditProfilePresenterCallback
    ): Boolean {
        if (!getIsCityInputCorrect(city)) {
            editProfilePresenterCallback.cityEditProfileInputError()
            return false
        }
        return true
    }
}
