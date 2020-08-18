package com.bunbeauty.ideal.myapplication.clean_architecture.business.editing.profile

import android.content.Intent
import android.util.Log
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag.FB_TAG
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.VerifyPhoneNumberApi
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.VerifyPhoneNumberCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.EditProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UpdateUsersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.FirebaseDatabase

class EditProfileInteractor(
    private val intent: Intent,
    private val userRepository: UserRepository,
    private val verifyPhoneNumberApi: VerifyPhoneNumberApi
) : UpdateUsersCallback, VerifyPhoneNumberCallback, UserCallback {

    private lateinit var editProfilePresenterCallback: EditProfilePresenterCallback

    lateinit var cacheUser: User
    lateinit var cacheWithChangesUser: User

    fun getUser(editProfilePresenterCallback: EditProfilePresenterCallback) {
        cacheUser = intent.getSerializableExtra(User.USER) as User
        editProfilePresenterCallback.showEditProfile(cacheUser)
    }

    fun saveData(
        user: User,
        photos: List<Photo>,
        editProfilePresenterCallback: EditProfilePresenterCallback
    ) {
        this.editProfilePresenterCallback = editProfilePresenterCallback
        cacheWithChangesUser = user

        if (isNameCorrect(user.name, editProfilePresenterCallback) &&
            isSurnameCorrect(user.surname, editProfilePresenterCallback)
        ) {
            //что будет если несколько добавить, зарузит несколько фоток?
            if (photos.isNotEmpty()) {
                editProfilePresenterCallback.showLoading()
                editProfilePresenterCallback.deletePreviousPhoto(
                    arrayListOf(Photo(id = cacheUser.id, link = cacheUser.photoLink))
                )
                editProfilePresenterCallback.savePhotos(
                    photos, user
                )
                return
            }

            if (cacheUser.phone == user.phone) {
                userRepository.update(user, this)
            } else {
                cacheUser = user
                checkPhoneAlreadyUsed(user.phone)
            }
        }
    }

    override fun returnUpdatedCallback(user: User) {
        editProfilePresenterCallback.goToProfile(user)
    }

    private fun isNameCorrect(
        name: String,
        editProfilePresenterCallback: EditProfilePresenterCallback
    ): Boolean {
        if (name.isEmpty()) {
            editProfilePresenterCallback.nameEditProfileInputErrorEmpty()
            return false
        }

        if (!isNameInputCorrect(name)) {
            editProfilePresenterCallback.nameEditProfileInputError()
            return false
        }

        if (!isNameLengthLessTwenty(name)) {
            editProfilePresenterCallback.nameEditProfileInputErrorLong()
            return false
        }
        return true
    }

    fun isNameInputCorrect(name: String): Boolean {
        if (!name.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    fun isNameLengthLessTwenty(name: String): Boolean {
        if (name.length > 20) {
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

        if (!isSurnameInputCorrect(surname)) {
            editProfilePresenterCallback.surnameEditProfileInputError()
            return false
        }

        if (!isSurnameLengthLessTwenty(surname)) {
            editProfilePresenterCallback.surnameEditProfileEditErrorLong()
            return false
        }
        return true
    }

    fun isSurnameInputCorrect(surname: String): Boolean {
        if (!surname.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    fun isSurnameLengthLessTwenty(surname: String): Boolean {
        if (surname.length > 20) {
            return false
        }
        return true
    }

    fun checkPhoneAlreadyUsed(phoneNumber: String) {
        userRepository.getByPhoneNumber(phoneNumber, this, true)
    }

    override fun returnGottenObject(element: User?) {
        if (element == null) return

        if (element.name.isEmpty()) {
            verifyPhoneNumberApi.sendVerificationCode(cacheUser.phone, this)
            editProfilePresenterCallback.returnCodeSent()
        } else {
            editProfilePresenterCallback.showPhoneAlreadyUsedError()
        }
    }


    fun resendCode(phoneNumber: String) {
        verifyPhoneNumberApi.resendVerificationCode(phoneNumber)
    }

    override fun returnTooManyRequestsError() {
        editProfilePresenterCallback.showTooManyRequestsError()
    }

    fun verifyCode(code: String) {
        verifyPhoneNumberApi.checkCode(code, this)
    }

    override fun returnTooShortCodeError() {
        editProfilePresenterCallback.showTooShortCodeError()
    }

    override fun returnVerificationFailed() {
        editProfilePresenterCallback.showVerificationFailed()
    }

    override fun returnCredential(credential: PhoneAuthCredential) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!

        currentUser.updatePhoneNumber(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userRepository.update(cacheUser, this)
            } else {
                editProfilePresenterCallback.showWrongCodeError()

                Log.d(FB_TAG, "updatePhoneNumber() failed: " + task.exception!!.message)
            }
        }
    }

    fun signOut() {
        val tokenRef = FirebaseDatabase
            .getInstance()
            .getReference(User.USERS)
            .child(User.getMyId())
            .child(User.TOKEN)
        tokenRef.setValue("-")
        FirebaseAuth.getInstance().signOut()
    }


}
