package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.profile.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo.PhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.PhotoSliderActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.AuthorizationActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.EditProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.EditProfileView
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_profile.*
import javax.inject.Inject

class EditProfileActivity : MvpAppCompatActivity(), ITopPanel, IBottomPanel, EditProfileView,
    IAdapterSpinner {

    override var panelContext: Activity = this

    @InjectPresenter
    lateinit var editProfilePresenter: EditProfilePresenter

    @Inject
    lateinit var editProfileInteractor: EditProfileInteractor

    @Inject
    lateinit var photoInteractor: PhotoInteractor

    @ProvidePresenter
    internal fun provideEditProfilePresenter(): EditProfilePresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .interactorModule(InteractorModule(intent))
            .firebaseModule(FirebaseModule())
            .build()
            .inject(this)
        return EditProfilePresenter(editProfileInteractor, photoInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        configViews()
        hideCodeInputAndButtons()
        createPanels()

        editProfilePresenter.getUser()
    }

    override fun onResume() {
        super.onResume()
        initBottomPanel()
    }

    private fun configViews() {
        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.cities)),
            cityEditProfileSpinner,
            this
        )
        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.countryCode)),
            codeEditProfileSpinner,
            this
        )

        saveChangesEditProfileBtn.setOnClickListener { saveChanges() }
        verifyCodeEditProfileBtn.setOnClickListener {
            editProfilePresenter.verifyCode(codeEditProfileInput.text.toString())
        }
        resendCodeEditProfileBtn.setOnClickListener {
            editProfilePresenter.resendCode(phoneEditProfileInput.text.toString())
        }
        avatarEditProfileImage.setOnClickListener {
            openPhoto()
        }

        photoEditProfileBtn.setOnClickListener {
            CropImage.activity().start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                editProfilePresenter.createPhoto(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun openPhoto() {
        val intent = Intent(this, PhotoSliderActivity::class.java).apply {
            putParcelableArrayListExtra(
                Photo.PHOTO,
                arrayListOf(Photo(link = editProfilePresenter.getCacheOwner().photoLink))
            )
            putExtra(Photo.LINK, editProfilePresenter.getCacheOwner().photoLink)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun createPanels() {
        initTopPanel("Редактирование профиля", ButtonTask.LOGOUT)
    }

    override fun showEditProfile(user: User) {
        nameEditProfileInput.setText(user.name)
        surnameEditProfileInput.setText(user.surname)
        phoneEditProfileInput.setText(user.phone.substring(2))
        showAvatar(user.photoLink)

        cityEditProfileSpinner.setText(user.city)
        (cityEditProfileSpinner.adapter as ArrayAdapter<String>).filter.filter("")
        codeEditProfileSpinner.setText(user.phone.substring(0, 2))
        (codeEditProfileSpinner.adapter as ArrayAdapter<String>).filter.filter("")
    }

    private fun saveChanges() {
        val phoneNumber =
            codeEditProfileSpinner.text.toString() + phoneEditProfileInput.text.toString().trim()

        editProfilePresenter.saveData(
            nameEditProfileInput.text.toString().trim(),
            surnameEditProfileInput.text.toString().trim(),
            cityEditProfileSpinner.text.toString(),
            phoneNumber
        )
    }

    override fun disableEditProfileEditButton() {
        saveChangesEditProfileBtn.isEnabled = false
    }

    override fun enableEditProfileEditButton() {
        saveChangesEditProfileBtn.isEnabled = true
    }

    override fun showPhoneError(error: String) {
        phoneEditProfileInput.error = error
        phoneEditProfileInput.requestFocus()
    }

    override fun setNameEditProfileInputError(error: String) {
        nameEditProfileInput.error = error
        nameEditProfileInput.requestFocus()
    }

    override fun setSurnameEditProfileInputError(error: String) {
        surnameEditProfileInput.error = error
        surnameEditProfileInput.requestFocus()
    }

    override fun setPhoneEditProfileInputError(error: String) {
        phoneEditProfileInput.error = error
        phoneEditProfileInput.requestFocus()
    }

    override fun showAvatar(photoLink: String) {
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        Picasso.get()
            .load(photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarEditProfileImage)
    }

    override fun showCodeInputAndButtons() {
        codeEditProfileLayout.visibility = View.VISIBLE
        verifyCodeEditProfileBtn.visibility = View.VISIBLE
        resendCodeEditProfileBtn.visibility = View.VISIBLE
        saveChangesEditProfileBtn.visibility = View.GONE
    }

    override fun hideCodeInputAndButtons() {
        codeEditProfileLayout.visibility = View.GONE
        verifyCodeEditProfileBtn.visibility = View.GONE
        resendCodeEditProfileBtn.visibility = View.GONE
        saveChangesEditProfileBtn.visibility = View.VISIBLE
    }

    override fun goToProfile(user: User) {
        val intent = Intent()
        intent.putExtra(User.USER, user)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun actionClick() {
        signOut()
    }

    private fun signOut() {
        editProfilePresenter.signOut()

        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)

        finish()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}