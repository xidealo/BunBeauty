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
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.AddingServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments.PremiumElementFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.BottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.TopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.AddingServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AddingServiceView
import com.bunbeauty.ideal.myapplication.createService.MyCalendar
import com.bunbeauty.ideal.myapplication.fragments.CategoryElement
import com.bunbeauty.ideal.myapplication.fragments.ServicePhotoElement
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder
import java.io.IOException
import java.util.*
import javax.inject.Inject

class AddingServiceActivity : MvpAppCompatActivity(), View.OnClickListener, AddingServiceView {

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

    @InjectPresenter
    lateinit var addingServicePresenter: AddingServicePresenter

    @ProvidePresenter
    internal fun provideAddingServicePresenter(): AddingServicePresenter {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(application, intent))
                .build().inject(this)

        return AddingServicePresenter(addingServiceInteractor)
    }

    @Inject
    lateinit var addingServiceInteractor: AddingServiceInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adding_service)

        init()
        showTopPanel()
        showBottomPanel()
        showCategory()
    }

    private fun showTopPanel() {
        val topPanel = TopPanel()

        topPanel.title = "Создание услуги"

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.topAddingServiceLayout, topPanel)
        transaction.commit()
    }

    private fun showBottomPanel() {
        val bottomPanel = BottomPanel()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.bottomAddServiceLayout, bottomPanel)
        transaction.commit()
    }

    private fun init() {
        findViewById<Button>(R.id.addServiceAddServiceBtn)
                .setOnClickListener(this)
        findViewById<TextView>(R.id.servicePhotoAddServiceImage)
                .setOnClickListener(this)

        nameServiceInput = findViewById(R.id.nameAddServiceInput)
        costAddServiceInput = findViewById(R.id.costAddServiceInput)
        descriptionServiceInput = findViewById(R.id.descriptionAddServiceInput)
        addressServiceInput = findViewById(R.id.addressAddServiceInput)
        premiumLayout = findViewById(R.id.premiumAddServiceLayout)
        mainLayout = findViewById(R.id.mainLayoutAddService)
        fpathOfImages = ArrayList()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addServiceAddServiceBtn -> {
                val service = addingServicePresenter.addService(
                        nameServiceInput.text.toString().toLowerCase(),
                        descriptionServiceInput.text.toString(),
                        costAddServiceInput.text.toString(),
                        categoryElement.category,
                        addressServiceInput.text.toString(),
                        categoryElement.tagsArray)

                if(service!=null){
                    addingServicePresenter.addImages(fpathOfImages, service)
                }
            }
            R.id.servicePhotoAddServiceImage -> chooseImage()
            else -> {
            }
        }
    }

    private fun chooseImage() {
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
                data != null && data.data != null) {
            try {
                //show image on activity
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                showImage(bitmap, data.data!!.toString())

                fpathOfImages.add(data.data!!.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // плохое место
    private fun showImage(bitmap: Bitmap, filePath: String) {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.feedAddServiceLayout, ServicePhotoElement(bitmap, filePath, "add service"))
                .commit()
    }

    fun removePhoto(servicePhotoElement: ServicePhotoElement, filePath: String) {
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
                .add(R.id.categoryAddServiceLayout, categoryElement)
                .commit()
    }

    override fun showPremiumBlock(service:Service) {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.premiumAddServiceLayout, PremiumElementFragment(service))
                .commit()

        premiumLayout.visibility = View.VISIBLE
    }

    override fun hideMainBlocks() {
        mainLayout.visibility = View.GONE
    }

    /*override fun goToMyCalendar(status: String, serviceId: String) {
        val intent = Intent(this, MyCalendar::class.java)
        intent.putExtra(SERVICE_ID, serviceId)
        intent.putExtra(STATUS_USER_BY_SERVICE, status)
        startActivity(intent)
        finish()
    }*/

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