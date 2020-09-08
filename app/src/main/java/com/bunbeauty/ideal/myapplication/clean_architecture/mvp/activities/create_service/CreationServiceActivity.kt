package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.create_service

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.ChangeablePhotoAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.CategoryFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.photoElement.IChangeablePhotoElement
import com.bunbeauty.ideal.myapplication.clean_architecture.business.create_service.CreationServiceServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.create_service.CreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.photo.PhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.PhotoSliderActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.ScheduleActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.fragments.PremiumFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.CreationServicePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.CreationServiceView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_creation_service.*
import javax.inject.Inject


class CreationServiceActivity : BaseActivity(), CreationServiceView, IChangeablePhotoElement {

    private lateinit var categoryFragment: CategoryFragment

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation_service)
        init()
        showMainBlock()
    }

    override fun onResume() {
        super.onResume()
        initBottomPanel()
    }

    private fun init() {
        activity_creation_service_rv_photos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activity_creation_service_rv_photos.adapter = changeablePhotoAdapter

        activity_creation_service_btn_add_service.setOnClickListener {
            creationServicePresenter.addService(
                activity_creation_service_et_name.text.toString().trim(),
                activity_creation_service_et_description.text.toString().trim(),
                activity_creation_service_et_cost.text.toString().toLongOrNull() ?: 0,
                activity_creation_service_et_address.text.toString().trim(),
                activity_creation_service_np_hour.value,
                activity_creation_service_np_minute.value,
                categoryFragment.getCategory(),
                categoryFragment.getSelectedTags()
            )
        }
        activity_creation_service_np_hour.minValue = 0
        activity_creation_service_np_hour.maxValue = 8
        activity_creation_service_np_minute.minValue = 0
        activity_creation_service_np_minute.maxValue = 1
        activity_creation_service_np_minute.value = 1
        activity_creation_service_np_minute.displayedValues = arrayOf("0", "30")

        activity_creation_service_btn_add_photo.setOnClickListener {
            CropImage.activity().start(this)
        }
        activity_creation_service_btn_continue.setOnClickListener {
            goToSchedule()
        }

        initTopPanel("Создание услуги", ButtonTask.NONE)
        categoryFragment =
            supportFragmentManager.findFragmentById(R.id.activity_creation_service_fg_category) as CategoryFragment
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                creationServicePresenter.createPhoto(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    override fun showPremiumBlock(service: Service) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.activity_creation_service_ll_premium, PremiumFragment.newInstance(service))
            .commit()
        activity_creation_service_ll_premium.visibility = View.VISIBLE
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

    override fun openPhoto(openedPhotoLinkOrUri: String) {
        val intent = Intent(this, PhotoSliderActivity::class.java).apply {
            putParcelableArrayListExtra(
                Photo.PHOTO,
                ArrayList(creationServicePresenter.getPhotosLink())
            )
            putExtra(Photo.LINK, openedPhotoLinkOrUri)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun updatePhotoFeed(photos: List<Photo>) {
        changeablePhotoAdapter.setData(
            creationServicePresenter.getPhotosLink(),
            this,
            resources.getDimensionPixelSize(R.dimen.photo_width),
            resources.getDimensionPixelSize(R.dimen.photo_height)
        )
    }

    override fun hideMainBlock() {
        activity_creation_service_ll_main.visibility = View.GONE
        activity_creation_service_btn_add_service.visibility = View.GONE
        activity_creation_service_btn_continue.visibility = View.VISIBLE
    }

    override fun showMainBlock() {
        activity_creation_service_ll_main.visibility = View.VISIBLE
        activity_creation_service_btn_add_service.visibility = View.VISIBLE
        activity_creation_service_btn_continue.visibility = View.GONE
    }

    override fun showMessage(message: String) {
        Snackbar.make(activity_creation_service_ll_main, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

    override fun showMoreTenImages() {
        Toast.makeText(this, "Должно быть меньше 10 фотографий", Toast.LENGTH_LONG).show()
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

    override fun showError(error: String) {
        Snackbar.make(activity_creation_service_ll_main, error, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.grey))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

    override fun showAddressInputError(error: String) {
        activity_creation_service_et_address.error = error
        activity_creation_service_et_address.requestFocus()
    }

    private fun goToSchedule() {
        val intent = Intent(this, ScheduleActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

}