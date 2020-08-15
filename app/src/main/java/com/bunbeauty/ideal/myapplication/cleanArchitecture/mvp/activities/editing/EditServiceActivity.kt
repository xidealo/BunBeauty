package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ChangeablePhotoAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.CategoryFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement.IChangeablePhotoElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo.PhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.service.EditServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.service.EditServiceTagInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.PhotoSliderActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.EditServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.EditServiceView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_service.*
import javax.inject.Inject

class EditServiceActivity : MvpAppCompatActivity(), IBottomPanel, ITopPanel,
    EditServiceView, IChangeablePhotoElement {

    override var panelContext: Activity = this

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
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)
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
        editServicePresenter.createEditServiceScreen()
        createPanels()
    }

    private fun init() {
        saveEditServiceBtn.setOnClickListener {
            editServicePresenter.save(
                nameEditServiceInput.text.toString().trim(),
                addressEditServiceInput.text.toString().trim(),
                descriptionEditServiceInput.text.toString().trim(),
                costEditServiceInput.text.toString().toLongOrNull() ?: 0,
                categoryFragment.getCategory(),
                categoryFragment.getSelectedTags()
            )
        }
        photoEditServiceBtn.setOnClickListener {
            CropImage.activity().start(this)
        }

        deleteEditServiceBtn.setOnClickListener {
            confirmDelete(editServicePresenter.getCacheService())
        }

        resultsEditServiceRecycleView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        resultsEditServiceRecycleView.adapter = changeablePhotoAdapter

        categoryFragment =
            supportFragmentManager.findFragmentById(R.id.categoryEditServiceLayout) as CategoryFragment
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

    override fun showEditService(service: Service) {
        nameEditServiceInput.append(service.name)
        addressEditServiceInput.append(service.address)
        costEditServiceInput.append(service.cost.toString())
        descriptionEditServiceInput.append(service.description)
        categoryFragment.setCategoryFragment(service.category, service.tags)
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
        progressEditServiceBar.visibility = View.VISIBLE
        editServiceScroll.visibility = View.VISIBLE
        saveEditServiceBtn.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressEditServiceBar.visibility = View.GONE
        editServiceScroll.visibility = View.VISIBLE
        saveEditServiceBtn.visibility = View.VISIBLE
    }

    override fun setNameEditServiceInputError(error: String) {
        nameEditServiceInput.error = error
        nameEditServiceInput.requestFocus()
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
        finish()
    }
}