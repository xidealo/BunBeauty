package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.profile.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo.IPhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo.IPhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo.PhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.EditProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.EditProfileView


@InjectViewState
class EditProfilePresenter(
    private val editProfileInteractor: EditProfileInteractor,
    private val photoInteractor: IPhotoInteractor
) :
    MvpPresenter<EditProfileView>(), EditProfilePresenterCallback, IPhotoCallback {

    fun getUser() {
        editProfileInteractor.getUser(this)
    }

    fun getCacheOwner() = editProfileInteractor.cacheUser

    override fun showEditProfile(user: User) {
        viewState.showEditProfile(user)
    }

    override fun goToProfile(user: User) {
        viewState.goToProfile(user)
    }

    fun saveData(name: String, surname: String, city: String, phone: String) {
        val user = editProfileInteractor.cacheUser.copy()
        user.name = name
        user.surname = surname
        user.city = city
        user.phone = phone
        editProfileInteractor.saveData(user, photoInteractor.getPhotosLink(), this)
    }

    override fun returnCreatedPhotoLink(uri: Uri) {
        editProfileInteractor.cacheWithChangesUser.photoLink = uri.toString()
        editProfileInteractor.saveData(
            editProfileInteractor.cacheWithChangesUser,
            photoInteractor.getPhotosLink(),
            this
        )
    }

    fun createPhoto(uri: Uri) {
        val photo = Photo()
        photo.link = uri.toString()
        photoInteractor.addPhoto(photo)
        viewState.showAvatar(photo.link)
    }

    override fun savePhotos(photos: List<Photo>, user: User) {
        photoInteractor.savePhotos(photos, user, this)
    }

    override fun returnPhotos(photos: List<Photo>) {}

    override fun nameEditProfileInputError() {
        viewState.enableEditProfileEditButton()
        viewState.setNameEditProfileInputError("Допустимы только буквы и тире")
    }

    override fun nameEditProfileInputErrorEmpty() {
        viewState.enableEditProfileEditButton()
        viewState.setNameEditProfileInputError("Введите своё имя")
    }

    override fun nameEditProfileInputErrorLong() {
        viewState.enableEditProfileEditButton()
        viewState.setNameEditProfileInputError("Слишком длинное имя")
    }

    override fun surnameEditProfileInputError() {
        viewState.enableEditProfileEditButton()
        viewState.setSurnameEditProfileInputError("Допустимы только буквы и тире")
    }

    override fun surnameEditProfileInputErrorEmpty() {
        viewState.enableEditProfileEditButton()
        viewState.setSurnameEditProfileInputError("Введите свою фамилию")
    }

    override fun surnameEditProfileEditErrorLong() {
        viewState.enableEditProfileEditButton()
        viewState.setSurnameEditProfileInputError("Слишком длинная фамилия")
    }

    override fun phoneEditProfileInputError() {
        viewState.enableEditProfileEditButton()
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
        viewState.showMessage("Лимит привышен. Повторите попытку позже")
    }

    override fun showTooShortCodeError() {
        viewState.showMessage("Код слишком короткий")
    }

    override fun showVerificationFailed() {
        viewState.showMessage("Что-то пошло не так")
    }

    override fun showWrongCodeError() {
        viewState.showMessage("Неверный код. Попробуйте ещё раз.")
    }

    override fun showPhoneAlreadyUsedError() {
        viewState.showMessage("Данный номер уже используется другим пользователем.")
    }

    override fun deletePreviousPhoto(photos: ArrayList<Photo>) {
        photoInteractor.deletePhotosFromStorage(User.USER_PHOTO, photos)
    }


    fun signOut() {
        editProfileInteractor.signOut()
    }


}