package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.editing

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.ChangeablePhotoAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.CategoryFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.photoElement.IChangeablePhotoElement
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.editing.service.EditServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.editing.service.EditServiceTagInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.photo.PhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.PhotoSliderActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in.EditServicePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.EditServiceView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_service.*
import javax.inject.Inject

class EditServiceActivity : BaseActivity(), EditServiceView, IChangeablePhotoElement {

    @Inject
    lateinit var changeablePhotoAdapter: ChangeablePhotoAdapter

    private lateinit var categoryFragment: CategoryFragment

    @Inject
    lateinit var editServiceServiceInteractor: EditServiceServiceInteractor

    @Inject
    lateinit var editServiceTagInteractor: EditServiceTagInteractor

    @Inject
    lateinit var photoInteractor: PhotoInteractor

    @InjectPresenter
    lateinit var editServicePresenter: EditServicePresenter

    @ProvidePresenter
    internal fun provideEditServicePresenter(): EditServicePresenter {
        buildDagger().inject(this)
        return EditServicePresenter(
            editServiceServiceInteractor,
            photoInteractor,
            editServiceTagInteractor
        )
    }

    private fun createPanels() {
        initTopPanel("Редактирование услуги", ButtonTask.NONE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_service)
        init()
        showLoading()
        editServicePresenter.getService()
        createPanels()
    }

    private fun init() {
        activity_edit_service_btn_save.setOnClickListener {
            editServicePresenter.saveService(
                activity_edit_service_et_name.text.toString().trim(),
                activity_edit_service_et_address.text.toString().trim(),
                activity_edit_service_et_description.text.toString().trim(),
                activity_edit_service_et_cost.text.toString().toLongOrNull() ?: 0,
                activity_edit_service_np_hour.value,
                activity_edit_service_np_minute.value,
                categoryFragment.getCategory(),
                categoryFragment.getSelectedTags()
            )
        }

        activity_edit_service_np_hour.minValue = 0
        activity_edit_service_np_hour.maxValue = 8
        activity_edit_service_np_minute.minValue = 0
        activity_edit_service_np_minute.maxValue = 1
        activity_edit_service_np_minute.displayedValues = arrayOf("0", "30")

        activity_edit_service_btn_add_photo.setOnClickListener {
            CropImage.activity().start(this)
        }

        activity_edit_service_btn_delete.setOnClickListener {
            confirmDelete(editServicePresenter.getCacheService())
        }

        activity_edit_service_rv_photos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activity_edit_service_rv_photos.adapter = changeablePhotoAdapter

        categoryFragment =
            supportFragmentManager.findFragmentById(R.id.activity_edit_service_fr_category) as CategoryFragment
    }

    override fun showEditService(service: Service) {
        activity_edit_service_et_name.append(service.name)
        activity_edit_service_et_address.append(service.address)
        activity_edit_service_et_cost.append(service.cost.toString())
        activity_edit_service_et_description.append(service.description)
        activity_edit_service_np_hour.value = (service.duration / 1).toInt()
        activity_edit_service_np_minute.value = if (service.duration % 1 == 0f) {
            0
        } else {
            1
        }

        categoryFragment.setCategoryFragment(service.category, service.tags)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                editServicePresenter.createPhoto(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun confirmDelete(service: Service) {
        MaterialAlertDialogBuilder(this, R.style.myDialogTheme)
            .setTitle("Внимание!")
            .setMessage(
                "Удалить услугу ${service.name}?"
            ).setPositiveButton("Удалить") { _, _ ->
                editServicePresenter.delete()
            }
            .setNegativeButton("Отмена") { _, _ -> }
            .show()
    }

    override fun onResume() {
        super.onResume()
        initBottomPanel()
    }

    override fun updatePhotoFeed() {
        changeablePhotoAdapter.setData(
            editServicePresenter.getPhotosLink(),
            this,
            resources.getDimensionPixelSize(R.dimen.photo_width),
            resources.getDimensionPixelSize(R.dimen.photo_height)
        )
    }

    override fun showLoading() {
        activity_edit_service_pb_loading.visible()
        activity_edit_service_sv_main.visible()
        activity_edit_service_btn_save.visible()
    }

    override fun hideLoading() {
        activity_edit_service_pb_loading.gone()
        activity_edit_service_sv_main.visible()
        activity_edit_service_btn_save.visible()
    }

    override fun setNameEditServiceInputError(error: String) {
        activity_edit_service_et_name.error = error
        activity_edit_service_et_name.requestFocus()
    }

    override fun deletePhoto(photo: Photo) {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle("Внимание!")
        dialog.setMessage("Удалить фотографию?")
        dialog.setCancelable(false)
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Да") { _, _ ->
            editServicePresenter.removePhoto(photo)
        }
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Нет") { _, _ -> }
        dialog.setIcon(android.R.drawable.ic_dialog_alert)
        dialog.show()
    }

    override fun showMessage(message: String) {
        Snackbar.make(activity_edit_service_ll_main, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

    override fun showNameInputError(error: String) {
        activity_edit_service_et_name.error = error
        activity_edit_service_et_name.requestFocus()
    }

    override fun showDescriptionInputError(error: String) {
        activity_edit_service_et_description.error = error
        activity_edit_service_et_description.requestFocus()
    }

    override fun showCostInputError(error: String) {
        activity_edit_service_et_cost.error = error
        activity_edit_service_et_cost.requestFocus()
    }

    override fun showError(error: String) {
        Snackbar.make(activity_edit_service_ll_main, error, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.grey))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

    override fun showAddressInputError(error: String) {
        activity_edit_service_et_address.error = error
        activity_edit_service_et_address.requestFocus()
    }

    override fun openPhoto(openedPhotoLinkOrUri: String) {
        val intent = Intent(this, PhotoSliderActivity::class.java).apply {
            putParcelableArrayListExtra(
                Photo.PHOTO,
                ArrayList(editServicePresenter.getPhotosLink())
            )
            putExtra(Photo.LINK, openedPhotoLinkOrUri)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun goToService(service: Service) {
        val intent = Intent()
        intent.putExtra(Service.SERVICE, service)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun goToProfile(service: Service) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}