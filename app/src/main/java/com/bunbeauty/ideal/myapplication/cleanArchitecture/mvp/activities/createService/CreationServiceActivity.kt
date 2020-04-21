package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments.PremiumElementFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IPhotoEditable
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.BottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.TopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.CreationServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AddingServiceView
import com.bunbeauty.ideal.myapplication.fragments.CategoryElement
import com.bunbeauty.ideal.myapplication.fragments.ServicePhotoElement
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi
import java.io.IOException
import java.util.*
import javax.inject.Inject

class CreationServiceActivity : MvpAppCompatActivity(), View.OnClickListener, AddingServiceView,
    IPhotoEditable, IBottomPanel, ITopPanel {

    private lateinit var photoCreationServiceBtn: TextView
    private lateinit var nameServiceInput: EditText
    private lateinit var costAddServiceInput: EditText
    private lateinit var descriptionServiceInput: EditText
    private lateinit var addressServiceInput: EditText
    private lateinit var premiumLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout

    //храним ссылки на картинки в хранилище
    private lateinit var fpathOfImages: ArrayList<String>

    private lateinit var categoryElement: CategoryElement
    private lateinit var continueButton: Button
    private lateinit var addServiceCreationServiceBtn: Button

    @InjectPresenter
    lateinit var creationServicePresenter: CreationServicePresenter

    @Inject
    lateinit var creationServiceInteractor: CreationServiceInteractor

    @ProvidePresenter
    internal fun provideAddingServicePresenter(): CreationServicePresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(application, intent))
            .build().inject(this)

        return CreationServicePresenter(creationServiceInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.creation_service)
        init()
        createPanels()
        showCategory()
    }

    private fun init() {
        fpathOfImages = ArrayList()
        nameServiceInput = findViewById(R.id.nameCreationServiceInput)
        costAddServiceInput = findViewById(R.id.costCreationServiceInput)
        descriptionServiceInput = findViewById(R.id.descriptionCreationServiceInput)
        addressServiceInput = findViewById(R.id.addressCreationServiceInput)
        premiumLayout = findViewById(R.id.premiumCreationServiceLayout)
        mainLayout = findViewById(R.id.mainCreationServiceLinearLayout)
        addServiceCreationServiceBtn = findViewById(R.id.addServiceCreationServiceBtn)
        addServiceCreationServiceBtn.setOnClickListener(this)
        photoCreationServiceBtn = findViewById(R.id.photoCreationServiceBtn)
        photoCreationServiceBtn.setOnClickListener(this)
    }

    private fun createPanels() {
        createBottomPanel(supportFragmentManager)
        createTopPanel("Создание услуги", ButtonTask.NONE, supportFragmentManager)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.addServiceCreationServiceBtn -> {
                creationServicePresenter.addService(
                    WorkWithStringsApi.firstCapitalSymbol(nameServiceInput.text.toString()),
                    descriptionServiceInput.text.toString(),
                    costAddServiceInput.text.toString(),
                    categoryElement.category,
                    addressServiceInput.text.toString(),
                    categoryElement.tagsArray
                )
                //creationServicePresenter.addImages(fpathOfImages, service)
            }
            R.id.photoCreationServiceBtn -> choosePhoto()
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
                //show image on activity
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                showPhoto(bitmap, data.data!!.toString())

                fpathOfImages.add(data.data!!.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // плохое место
    override fun showPhoto(bitmap: Bitmap, filePath: String) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.photoCreationServiceLayout, ServicePhotoElement(bitmap, filePath))
            .commit()
    }

    override fun removePhoto(servicePhotoElement: ServicePhotoElement, filePath: String) {
        supportFragmentManager
            .beginTransaction()
            .remove(servicePhotoElement)
            .commit()
        fpathOfImages.remove(filePath)
    }

    override fun showCategory() {
        categoryElement = CategoryElement(this)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.categoryCreationServiceLayout, categoryElement)
            .commit()
    }

    override fun showPremiumBlock(service: Service) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.premiumCreationServiceLayout, PremiumElementFragment(service))
            .commit()

        premiumLayout.visibility = View.VISIBLE
    }

    override fun hideMainBlocks() {
        mainLayout.visibility = View.GONE
    }

    override fun showAllDone() {
        Toast.makeText(this, "Сервис успешно создан", Toast.LENGTH_LONG).show()
    }

    override fun showMoreTenImages() {
        Toast.makeText(this, "Должно быть меньше 10 фотографий", Toast.LENGTH_LONG).show()
    }

    override fun showNameInputError(error: String) {
        nameServiceInput.error = error
        nameServiceInput.requestFocus()
    }

    override fun showDescriptionInputError(error: String) {
        descriptionServiceInput.error = error
        descriptionServiceInput.requestFocus()
    }

    override fun showCostInputError(error: String) {
        costAddServiceInput.error = error
        costAddServiceInput.requestFocus()
    }

    override fun showCategoryInputError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    override fun showAddressInputError(error: String) {
        addressServiceInput.error = error
        addressServiceInput.requestFocus()
    }

    companion object {
        private const val SERVICE_ID = "service id"
        private const val STATUS_USER_BY_SERVICE = "status UserCreateService"

        const val PICK_IMAGE_REQUEST = 71
        const val SERVICE_PHOTO = "service photo"
        const val CODES = "codes"
        const val CODE = "code"
        const val COUNT = "count"
    }
}