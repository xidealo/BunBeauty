package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.CategoryElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.ServicePhotoElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments.PremiumElementFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IPhotoEditable
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.CreationServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AddingServiceView
import kotlinx.android.synthetic.main.creation_service.*
import java.io.IOException
import javax.inject.Inject

class CreationServiceActivity : MvpAppCompatActivity(), View.OnClickListener, AddingServiceView,
    IPhotoEditable, IBottomPanel, ITopPanel {
    
    override var panelContext: Activity = this
    
    private lateinit var categoryElement: CategoryElement

    @InjectPresenter
    lateinit var creationServicePresenter: CreationServicePresenter

    @Inject
    lateinit var creationServiceServiceInteractor: CreationServiceServiceServiceInteractor

    @Inject
    lateinit var creationServiceTagInteractor: CreationServiceTagInteractor

    @Inject
    lateinit var creationServicePhotoInteractor: CreationServicePhotoInteractor

    @ProvidePresenter
    internal fun provideAddingServicePresenter(): CreationServicePresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(application, intent))
            .build().inject(this)

        return CreationServicePresenter(
            creationServiceServiceInteractor,
            creationServiceTagInteractor,
            creationServicePhotoInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.creation_service)
        init()
        createPanels()
        showCategory()
        showMainBlock()
    }

    override fun onResume() {
        super.onResume()
        initBottomPanel()
    }

    private fun init() {
        addServiceCreationServiceBtn.setOnClickListener(this)
        photoCreationServiceBtn.setOnClickListener(this)
        continueCreationServiceBtn.setOnClickListener(this)
    }

    private fun createPanels() {
        initTopPanel("Создание услуги", ButtonTask.NONE)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addServiceCreationServiceBtn -> {
                creationServicePresenter.addService(
                    nameCreationServiceInput.text.toString(),
                    descriptionCreationServiceInput.text.toString(),
                    costCreationServiceInput.text.toString().toLongOrNull() ?: 0,
                    categoryElement.category,
                    addressCreationServiceInput.text.toString(),
                    categoryElement.tagsArray
                )
            }
            R.id.photoCreationServiceBtn -> choosePhoto()

            R.id.continueCreationServiceBtn -> goToSchedule(Service())
        }
    }

    override fun choosePhoto() {
        //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        //Тип получаемых объектов - image:
        photoPickerIntent.type = "image/*"
        //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                showPhoto(bitmap, data.data!!.toString())
                creationServicePresenter.addImageLink(data.data!!.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun showPremiumBlock(service: Service) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.premiumCreationServiceLayout, PremiumElementFragment.newInstance(service))
            .commit()
        premiumCreationServiceLayout.visibility = View.VISIBLE
    }

    override fun showPhoto(bitmap: Bitmap, filePath: String) {
        supportFragmentManager
            .beginTransaction()
            .add(
                R.id.photoCreationServiceLayout,
                ServicePhotoElement.newInstance(bitmap, filePath)
            )
            .commit()
    }

    override fun removePhoto(servicePhotoElement: ServicePhotoElement, filePath: String) {
        supportFragmentManager
            .beginTransaction()
            .remove(servicePhotoElement)
            .commit()

        creationServicePresenter.removeImageLink(filePath)
    }

    override fun showCategory() {
        categoryElement = CategoryElement(this)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.categoryCreationServiceLayout, categoryElement)
            .commit()
    }

    override fun hideMainBlock() {
        mainCreationServiceLinearLayout.visibility = View.GONE
        addServiceCreationServiceBtn.visibility = View.GONE
        continueCreationServiceBtn.visibility = View.VISIBLE
    }

    override fun showMainBlock() {
        mainCreationServiceLinearLayout.visibility = View.VISIBLE
        addServiceCreationServiceBtn.visibility = View.VISIBLE
        continueCreationServiceBtn.visibility = View.GONE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showMoreTenImages() {
        Toast.makeText(this, "Должно быть меньше 10 фотографий", Toast.LENGTH_LONG).show()
    }

    override fun showNameInputError(error: String) {
        nameCreationServiceInput.error = error
        nameCreationServiceInput.requestFocus()
    }

    override fun showDescriptionInputError(error: String) {
        descriptionCreationServiceInput.error = error
        descriptionCreationServiceInput.requestFocus()
    }

    override fun showCostInputError(error: String) {
        costCreationServiceInput.error = error
        costCreationServiceInput.requestFocus()
    }

    override fun showCategoryInputError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    override fun showAddressInputError(error: String) {
        addressCreationServiceInput.error = error
        addressCreationServiceInput.requestFocus()
    }

    fun goToSchedule(service: Service) {

    }

    companion object {
        const val PICK_IMAGE_REQUEST = 71
        const val SERVICE_PHOTO = "service photo"
        const val CODES = "codes"
        const val CODE = "code"
        const val COUNT = "count"
    }
}