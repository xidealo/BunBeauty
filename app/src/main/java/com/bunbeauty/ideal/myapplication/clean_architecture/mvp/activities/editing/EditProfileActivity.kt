package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.editing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.editing.profile.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.photo.PhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.PhotoSliderActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in.AuthorizationActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.EditProfilePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.EditProfileView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_profile.*
import javax.inject.Inject

class EditProfileActivity : BaseActivity(), EditProfileView, IAdapterSpinner {

    @InjectPresenter
    lateinit var editProfilePresenter: EditProfilePresenter

    @Inject
    lateinit var editProfileInteractor: EditProfileInteractor

    @Inject
    lateinit var photoInteractor: PhotoInteractor

    @ProvidePresenter
    internal fun provideEditProfilePresenter(): EditProfilePresenter {
        buildDagger().inject(this)
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
            activity_edit_profile_sp_city,
            this
        )
        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.country_codes)),
            activity_edit_profile_sp_code,
            this
        )
        activity_edit_profile_btn_save.setOnClickListener {
            saveChanges()
            activity_edit_profile_btn_save.showLoading()
        }
        activity_edit_profile_btn_verify.setOnClickListener {
            editProfilePresenter.verifyCode(activity_edit_profile_et_code.text.toString())
        }
        activity_edit_profile_btn_resend.setOnClickListener {
            editProfilePresenter.resendCode(activity_edit_profile_et_phone.text.toString())
        }
        activity_edit_profile_iv_avatar.setOnClickListener {
            openPhoto()
        }
        activity_edit_profile_btn_add_photo.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(150, 150)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this)
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
        activity_edit_profile_et_name.setText(user.name)
        activity_edit_profile_et_surname.setText(user.surname)
        activity_edit_profile_et_phone.setText(user.phone.substring(2))
        showAvatar(user.photoLink)

        activity_edit_profile_sp_city.setText(user.city)
        (activity_edit_profile_sp_city.adapter as ArrayAdapter<String>).filter.filter("")
        activity_edit_profile_sp_code.setText(user.phone.substring(0, 2))
        (activity_edit_profile_sp_code.adapter as ArrayAdapter<String>).filter.filter("")
    }

    private fun saveChanges() {
        val phoneNumber =
            activity_edit_profile_sp_code.text.toString() + activity_edit_profile_et_phone.text.toString()
                .trim()

        editProfilePresenter.saveData(
            activity_edit_profile_et_name.text.toString().trim(),
            activity_edit_profile_et_surname.text.toString().trim(),
            activity_edit_profile_sp_city.text.toString(),
            phoneNumber
        )
    }

    override fun showPhoneError(error: String) {
        activity_edit_profile_et_phone.error = error
        activity_edit_profile_et_phone.requestFocus()
        activity_edit_profile_btn_save.hideLoading()
    }

    override fun setNameEditProfileInputError(error: String) {
        activity_edit_profile_et_name.error = error
        activity_edit_profile_et_name.requestFocus()
        activity_edit_profile_btn_save.hideLoading()
    }

    override fun setSurnameEditProfileInputError(error: String) {
        activity_edit_profile_et_surname.error = error
        activity_edit_profile_et_surname.requestFocus()
        activity_edit_profile_btn_save.hideLoading()
    }

    override fun setPhoneEditProfileInputError(error: String) {
        activity_edit_profile_et_phone.error = error
        activity_edit_profile_et_phone.requestFocus()
        activity_edit_profile_btn_save.hideLoading()
    }

    override fun showAvatar(photoLink: String) {
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        Picasso.get()
            .load(photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(activity_edit_profile_iv_avatar)
    }

    override fun showCodeInputAndButtons() {
        activity_edit_profile_til_code.visibility = View.VISIBLE
        activity_edit_profile_btn_verify.visibility = View.VISIBLE
        activity_edit_profile_btn_resend.visibility = View.VISIBLE
        activity_edit_profile_btn_save.visibility = View.GONE
    }

    override fun hideCodeInputAndButtons() {
        activity_edit_profile_til_code.visibility = View.GONE
        activity_edit_profile_btn_verify.visibility = View.GONE
        activity_edit_profile_btn_resend.visibility = View.GONE
        activity_edit_profile_btn_save.visibility = View.VISIBLE
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
        Snackbar.make(activity_edit_service_ll_main, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }
}