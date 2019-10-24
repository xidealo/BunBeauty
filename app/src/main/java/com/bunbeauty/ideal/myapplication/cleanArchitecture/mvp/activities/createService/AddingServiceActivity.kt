package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.AddingServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.TagFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.TagDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments.PremiumElementFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.AddingServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AddingServiceView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.TagRepository
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

    private lateinit var premiumText: TextView
    private lateinit var noPremiumText: TextView
    //храним ссылки на картинки в хранилище
    private lateinit var fpathOfImages: ArrayList<Uri>

    private lateinit var manager: FragmentManager
    private lateinit var categoryElement: CategoryElement

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
    @Inject
    lateinit var serviceRepository: ServiceRepository
    @Inject
    lateinit var serviceDao: ServiceDao
    @Inject
    lateinit var serviceFirebaseApi: ServiceFirebaseApi

    @Inject
    lateinit var tagRepository: TagRepository
    @Inject
    lateinit var tagDao: TagDao
    @Inject
    lateinit var tagFirebase: TagFirebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adding_service)

        init()
    }

    private fun init() {
        val addServicesBtn = findViewById<Button>(R.id.addServiceAddServiceBtn)
        val serviceImage = findViewById<TextView>(R.id.servicePhotoAddServiceImage)
        nameServiceInput = findViewById(R.id.nameAddServiceInput)
        costAddServiceInput = findViewById(R.id.costAddServiceInput)
        descriptionServiceInput = findViewById(R.id.descriptionAddServiceInput)
        addressServiceInput = findViewById(R.id.addressAddServiceInput)
        premiumText = findViewById(R.id.yesPremiumAddServiceText)

        noPremiumText = findViewById(R.id.noPremiumAddServiceText)
        premiumLayout = findViewById(R.id.premiumAddServiceLayout)

        manager = supportFragmentManager

        val premiumElement = PremiumElementFragment()
        val transaction = manager.beginTransaction()
        transaction.add(R.id.premiumAddServiceLayout, premiumElement)
        categoryElement = CategoryElement(this)
        transaction.add(R.id.categoryAddServiceLayout, categoryElement)
        transaction.commit()

        fpathOfImages = ArrayList()

        addServicesBtn.setOnClickListener(this)
        serviceImage.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addServiceAddServiceBtn -> {
                val serviceId = addingServicePresenter.addService(
                        nameServiceInput.text.toString().toLowerCase(),
                        descriptionServiceInput.text.toString(),
                        costAddServiceInput.text.toString(),
                        categoryElement.category,
                        addressServiceInput.text.toString(),
                        categoryElement.tagsArray)

                addingServicePresenter.addImages(fpathOfImages, serviceId)
            }
            R.id.servicePhotoAddServiceImage -> chooseImage()
            else -> {
            }
        }
    }

    override fun showPremiumBlock() {
        premiumLayout.visibility = View.VISIBLE
    }

    override fun hideMainBlocks() {
        //premiumLayout.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        val panelBuilder = PanelBuilder()
        panelBuilder.buildFooter(manager, R.id.footerAddServiceLayout)
        panelBuilder.buildHeader(manager, "Создание услуги", R.id.headerAddServiceLayout)
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.data != null) {
            val filePath = data.data
            try {
                //установка картинки на activity
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                addToScreen(bitmap, filePath)
                //serviceImage.setImageBitmap(bitmap);
                //загрузка картинки в fireStorage
                fpathOfImages.add(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun addToScreen(bitmap: Bitmap, filePath: Uri?) {
        val transaction = manager.beginTransaction()
        val servicePhotoElement = ServicePhotoElement(bitmap, filePath, "add service")
        transaction.add(R.id.feedAddServiceLayout, servicePhotoElement)
        transaction.commit()
    }

    fun deleteFragment(fr: ServicePhotoElement, filePath: Uri) {
        val transaction = manager.beginTransaction()
        transaction.remove(fr)
        transaction.commit()
        fpathOfImages.remove(filePath)
    }

   /* fun setPremium() {
        //service.setIsPremium(true);
        setWithPremium()
        premiumLayout.visibility = View.GONE
        premiumDate = addSevenDayPremium(WorkWithTimeApi.getDateInFormatYMDHMS(Date()))
        showPremiumActivated()
    }
*/

    override fun setWithPremium() {
        noPremiumText.visibility = View.GONE
        premiumText.visibility = View.VISIBLE
        premiumText.isEnabled = false
    }

    override fun goToMyCalendar(status: String, serviceId: String) {
        val intent = Intent(this, MyCalendar::class.java)
        intent.putExtra(SERVICE_ID, serviceId)
        intent.putExtra(STATUS_USER_BY_SERVICE, status)
        startActivity(intent)
        finish()
    }

    override fun showAllDone() {
        Toast.makeText(this, "Сервис успешно создан", Toast.LENGTH_LONG).show()
    }

    override fun showWrongCode() {
        Toast.makeText(this, "Неверно введен код", Toast.LENGTH_LONG).show()
    }

    override fun showOldCode() {
        Toast.makeText(this, "Код больше не действителен", Toast.LENGTH_LONG).show()
    }

    override fun showPremiumActivated() {
        Toast.makeText(this, "Премиум активирован", Toast.LENGTH_LONG).show()
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