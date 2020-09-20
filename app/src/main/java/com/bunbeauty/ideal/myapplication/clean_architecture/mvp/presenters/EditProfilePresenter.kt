package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.editing.profile.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.photo.IPhotoCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.photo.IPhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.EditProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.StringApi
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.EditProfileView


@InjectViewState
class EditProfilePresenter(
    private val editProfileInteractor: EditProfileInteractor,
    private val photoInteractor: IPhotoInteractor,
    private val stringApi: StringApi,
    private val intent: Intent,
    private val context: Context,
) :
    MvpPresenter<EditProfileView>(), EditProfilePresenterCallback, IPhotoCallback {

    fun getUser() {
        editProfileInteractor.getUser(intent,this)
    }

    fun getCacheOwner() = editProfileInteractor.cacheUser

    override fun showEditProfile(user: User) {
        viewState.showEditProfile(user)
    }

    override fun goToProfile(user: User) {
        viewState.goToProfile(user)
    }

    fun saveData(name: String, surname: String, city: String, code: String, phone: String) {
        val user = editProfileInteractor.cacheUser.copy()
        user.name = name
        user.surname = surname
        user.city = city
        user.phone = code + stringApi.getPhoneNumberDigits(phone)
        editProfileInteractor.saveData(user, photoInteractor.getPhotoLinkList(), this)
    }

    override fun returnCreatedPhotoLink(uri: Uri) {
        editProfileInteractor.cacheWithChangesUser.photoLink = uri.toString()
        editProfileInteractor.saveData(
            editProfileInteractor.cacheWithChangesUser,
            photoInteractor.getPhotoLinkList(),
            this
        )
    }

    fun createPhoto(uri: Uri) {
        val photo = Photo()
        photo.link = uri.toString()
        photoInteractor.addPhoto(photo)
        editProfileInteractor.cacheUser.photoLink = uri.toString()
        viewState.showAvatar(photo.link)
    }

    override fun savePhotos(photos: List<Photo>, user: User) {
        photoInteractor.savePhotos(photos, user, this)
    }

    override fun returnPhotos(photos: List<Photo>) {}

    override fun nameEditProfileInputError() {
        viewState.setNameEditProfileInputError("Допустимы только буквы и тире")
    }

    override fun nameEditProfileInputErrorEmpty() {
        viewState.setNameEditProfileInputError("Введите своё имя")
    }

    override fun nameEditProfileInputErrorLong() {
        viewState.setNameEditProfileInputError("Слишком длинное имя")
    }

    override fun surnameEditProfileInputError() {
        viewState.setSurnameEditProfileInputError("Допустимы только буквы и тире")
    }

    override fun surnameEditProfileInputErrorEmpty() {
        viewState.setSurnameEditProfileInputError("Введите свою фамилию")
    }

    override fun surnameEditProfileEditErrorLong() {
        viewState.setSurnameEditProfileInputError("Слишком длинная фамилия")
    }

    override fun phoneEditProfileInputError() {
        viewState.showPhoneError("Некорректный номер телефона")
    }

    override fun returnCodeSent() {
        viewState.showCodeInputAndButtons()
    }

    fun resendCode(phoneNumber: String) {
        editProfileInteractor.resendCode(phoneNumber)
    }

    fun verifyCode(code: String) {
        editProfileInteractor.verifyCode(code)
    }

    override fun showTooManyRequestsError() {
        viewState.showMessage(context.resources.getString(R.string.too_many_requests_error))
    }

    override fun showTooShortCodeError() {
        viewState.showMessage(context.resources.getString(R.string.too_short_code_error))
    }

    override fun showVerificationFailed() {
        viewState.showMessage(context.resources.getString(R.string.verification_failed_error))
    }

    override fun showWrongCodeError() {
        viewState.showMessage(context.resources.getString(R.string.wrong_code_error))
    }

    override fun showServiceConnectionProblem() {
        viewState.showMessage(context.resources.getString(R.string.server_connection_error))
    }

    override fun showPhoneAlreadyUsedError() {
        viewState.showMessage(context.resources.getString(R.string.phone_already_used_error))
        viewState.hideLoading()
    }

    override fun deletePreviousPhoto(photos: ArrayList<Photo>) {
        photoInteractor.deletePhotosFromStorage(User.USER_PHOTO, photos)
    }

    fun signOut() {
        editProfileInteractor.signOut()
    }

}