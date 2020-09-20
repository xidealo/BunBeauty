package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.create_service

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.create_service.CreationServiceServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.create_service.CreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.photo.PhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.PhotoSliderActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.ScheduleActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.ChangeablePhotoAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.CategoryFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.photoElement.EditablePhotoActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.PremiumFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.CreationServicePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.CreationServiceView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_creation_service.*
import javax.inject.Inject

class CreationServiceActivity : BaseActivity(), CreationServiceView, EditablePhotoActivity {

    @Inject
    lateinit var changeablePhotoAdapter: ChangeablePhotoAdapter

    @InjectPresenter
    lateinit var creationServicePresenter: CreationServicePresenter

    @Inject
    lateinit var creationServiceServiceInteractor: CreationServiceServiceServiceInteractor

    @Inject
    lateinit var creationServiceTagInteractor: CreationServiceTagInteractor

    @Inject
    lateinit var photoInteractor: PhotoInteractor

    @ProvidePresenter
    internal fun provideAddingServicePresenter(): CreationServicePresenter {
        buildDagger().inject(this)
        return CreationServicePresenter(
            creationServiceServiceInteractor,
            creationServiceTagInteractor,
            photoInteractor
        )
    }

    private lateinit var categoryFragment: CategoryFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation_service)

        setupUI()
        showMainBlock()
    }

    override fun onResume() {
        super.onResume()
        initBottomPanel()
    }

    private fun setupUI() {
        categoryFragment =
            supportFragmentManager.findFragmentById(R.id.activity_creation_service_fg_category) as CategoryFragment

        activity_creation_service_rv_photos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        changeablePhotoAdapter.editablePhotoActivity = this
        activity_creation_service_rv_photos.adapter = changeablePhotoAdapter

        activity_creation_service_btn_add_service.setOnClickListener {
            creationServicePresenter.addService(
                activity_creation_service_et_name.text.toString().trim(),
                activity_creation_service_et_description.text.toString().trim(),
                activity_creation_service_et_cost.text.toString().toLongOrNull() ?: 0,
                activity_creation_service_et_address.text.toString().trim(),
                activity_creation_service_np_hour.value,
                activity_creation_service_np_minute.value,
                categoryFragment.category,
                categoryFragment.selectedTagList
            )
        }
        setupDurationPickers()

        activity_creation_service_btn_continue.setOnClickListener {
            goToSchedule()
        }

        initTopPanel("Создание услуги", ButtonTask.NONE)
    }

    fun setupDurationPickers() {
        activity_creation_service_np_hour.minValue = 0
        activity_creation_service_np_hour.maxValue = 8
        activity_creation_service_np_minute.minValue = 0
        activity_creation_service_np_minute.maxValue = 1
        activity_creation_service_np_minute.value = 1
        activity_creation_service_np_minute.displayedValues = arrayOf("0", "30")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                creationServicePresenter.createPhoto(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(Tag.ERROR_TAG, result.error.printStackTrace().toString())
            }
        }
    }

    override fun showPremiumBlock(service: Service) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.activity_creation_service_ll_premium, PremiumFragment.newInstance(service))
            .commit()
        activity_creation_service_ll_premium.visible()
    }

    override fun deletePhoto(photo: Photo) {
        MaterialAlertDialogBuilder(this, R.style.myDialogTheme)
            .setTitle("Внимание!")
            .setMessage("Удалить фотографию?")
            .setPositiveButton("Да") { _, _ ->
                creationServicePresenter.removePhoto(photo)
            }.setNegativeButton("Нет") { _, _ ->
            }.show()
    }

    override fun addPhoto() {
        CropImage.activity().start(this)
    }

    override fun openPhoto(openedPhotoLinkOrUri: String) {
        val intent = Intent(this, PhotoSliderActivity::class.java).apply {
            putParcelableArrayListExtra(Photo.PHOTOS, creationServicePresenter.getPhotoLinkList())
            putExtra(Photo.LINK, openedPhotoLinkOrUri)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun updatePhotoFeed(photoLinkList: List<Photo>) {
        changeablePhotoAdapter.setData(
            photoLinkList,
            this,
            resources.getDimensionPixelSize(R.dimen.photo_width),
            resources.getDimensionPixelSize(R.dimen.photo_height)
        )
    }

    override fun hideMainBlock() {
        activity_creation_service_ll_main.gone()
        activity_creation_service_btn_add_service.gone()
        activity_creation_service_btn_continue.visible()
    }

    override fun showMainBlock() {
        activity_creation_service_ll_main.visible()
        activity_creation_service_btn_add_service.visible()
        activity_creation_service_btn_continue.gone()
    }

    override fun showNameInputError(error: String) {
        activity_creation_service_et_name.error = error
        activity_creation_service_et_name.requestFocus()
    }

    override fun showDescriptionInputError(error: String) {
        activity_creation_service_et_description.error = error
        activity_creation_service_et_description.requestFocus()
    }

    override fun showCostInputError(error: String) {
        activity_creation_service_et_cost.error = error
        activity_creation_service_et_cost.requestFocus()
    }

    override fun showCategorySpinnerError(error: String) {
        categoryFragment.showCategorySpinnerError(error)
    }

    override fun showAddressInputError(error: String) {
        activity_creation_service_et_address.error = error
        activity_creation_service_et_address.requestFocus()
    }

    override fun showMessage(message: String) {
        super.showMessage(message, activity_creation_service_ll_main)
    }

    private fun goToSchedule() {
        val intent = Intent(this, ScheduleActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}