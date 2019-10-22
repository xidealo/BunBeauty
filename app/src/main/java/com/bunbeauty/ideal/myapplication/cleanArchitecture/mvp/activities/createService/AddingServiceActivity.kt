package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService

import android.app.Activity
import android.content.ContentValues
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
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.TagDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.AddingServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AddingServiceView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.TagRepository
import com.bunbeauty.ideal.myapplication.createService.MyCalendar
import com.bunbeauty.ideal.myapplication.fragments.CategoryElement
import com.bunbeauty.ideal.myapplication.fragments.PremiumElement
import com.bunbeauty.ideal.myapplication.fragments.ServicePhotoElement
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.other.IPremium
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.*
import javax.inject.Inject

class AddingServiceActivity : MvpAppCompatActivity(), View.OnClickListener, IPremium, AddingServiceView {

    private lateinit var nameServiceInput: EditText
    private lateinit var costAddServiceInput: EditText
    private lateinit var descriptionServiceInput: EditText
    private lateinit var addressServiceInput: EditText
    private lateinit var premiumLayout: LinearLayout

    private lateinit var premiumText: TextView
    private lateinit var noPremiumText: TextView
    //храним ссылки на картинки в хранилище
    private lateinit var fpath: ArrayList<Uri>

    private lateinit var manager: FragmentManager
    private lateinit var premiumDate: String
    private lateinit var dbHelper: DBHelper
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

        val premiumElement = PremiumElement()
        val transaction = manager.beginTransaction()
        transaction.add(R.id.premiumAddServiceLayout, premiumElement)
        categoryElement = CategoryElement(this)
        transaction.add(R.id.categoryAddServiceLayout, categoryElement)
        transaction.commit()

        //isPremiumLayoutSelected = false
        dbHelper = DBHelper(this)
        fpath = ArrayList()

        addServicesBtn.setOnClickListener(this)
        serviceImage.setOnClickListener(this)
        premiumText.setOnClickListener(this)
        noPremiumText.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addServiceAddServiceBtn -> {
                addingServicePresenter.addService(
                        nameServiceInput.text.toString().toLowerCase(),
                        descriptionServiceInput.text.toString(),
                        costAddServiceInput.text.toString(),
                        categoryElement.category,
                        addressServiceInput.text.toString(),
                        categoryElement.tagsArray)

                //less than 10 images
               /* if (fpath.size <= MAX_COUNT_OF_IMAGES) {
                   uploadService(service)
                } else {
                    showMoreTenImages()
                }*/
            }
            R.id.servicePhotoAddServiceImage -> chooseImage()
            R.id.noPremiumAddServiceText -> showPremium()
            R.id.yesPremiumAddServiceText -> showPremium()
            else -> {
            }
        }
    }

    private fun uploadService(service: Service) {
        //items[Service.CREATION_DATE] = workWithTimeApi.getDateInFormatYMDHMS(Date())
        /*  for (String tag : service.getTags()) {
            tagsMap.put(String.valueOf(tag.hashCode()), tag);
        }
        items.put(Service.TAGS, tagsMap);*/

        //service.id = serviceId
        addServiceInLocalStorage(service)

        for (path in fpath) {
            uploadImage(path, "")
        }
    }

    override fun onResume() {
        super.onResume()

        /*if (service.getPremiumDate()) {
            setWithPremium();
        }*/

        val panelBuilder = PanelBuilder()
        panelBuilder.buildFooter(manager, R.id.footerAddServiceLayout)
        panelBuilder.buildHeader(manager, "Создание услуги", R.id.headerAddServiceLayout)
    }

    private fun addServiceInLocalStorage(service: Service) {

        val database = dbHelper.writableDatabase

        //добавление в БД
        val contentValues = ContentValues()
        contentValues.put(DBHelper.KEY_ID, service.id)
        contentValues.put(DBHelper.KEY_NAME_SERVICES, service.name.toLowerCase())
        contentValues.put(DBHelper.KEY_RATING_SERVICES, "0")
        contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, service.cost)
        contentValues.put(DBHelper.KEY_DESCRIPTION_SERVICES, service.description)
        contentValues.put(DBHelper.KEY_USER_ID, service.userId)
        contentValues.put(DBHelper.KEY_CATEGORY_SERVICES, service.category)
        contentValues.put(DBHelper.KEY_ADDRESS_SERVICES, service.address)

        database.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValues)

        contentValues.clear()
        contentValues.put(DBHelper.KEY_SERVICE_ID_TAGS, service.id)
        /*   for (String tag : service.getTags()) {
            contentValues.put(DBHelper.KEY_TAG_TAGS, tag);
            database.insert(DBHelper.TABLE_TAGS, null, contentValues);
        }
*/
        goToMyCalendar(getString(R.string.status_worker), service.id)
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
                fpath.add(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun uploadImage(filePath: Uri?, serviceId: String) {
        val firebaseStorage = FirebaseStorage.getInstance()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(User.USERS)

        if (filePath != null) {
            val photoId = myRef.push().key!!
            val storageReference = firebaseStorage.getReference("$SERVICE_PHOTO/$photoId")
            storageReference.putFile(filePath).addOnSuccessListener { storageReference.downloadUrl.addOnSuccessListener { uri -> uploadPhotos(uri.toString(), serviceId, photoId) } }.addOnFailureListener { }
        }
    }

    private fun uploadPhotos(storageReference: String, serviceId: String, photoId: String) {
/*
         val database = FirebaseDatabase.getInstance()
         val myRef = database.getReference(User.USERS)
                 .child(userId)
                 .child(Service.SERVICES)
                 .child(serviceId)
                 .child(PHOTOS)
                 .child(photoId)

         val items = HashMap<String, Any>()
         items[PHOTO_LINK] = storageReference
         myRef.updateChildren(items)

         val photo = Photo()
         photo.photoId = photoId
         photo.photoLink = storageReference
         photo.photoOwnerId = serviceId
         addPhotoInLocalStorage(photo)*/
    }

    private fun addPhotoInLocalStorage(photo: Photo) {

        val database = dbHelper.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(DBHelper.KEY_ID, photo.photoId)
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.photoLink)
        contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS, photo.photoOwnerId)

        database.insert(DBHelper.TABLE_PHOTOS, null, contentValues)
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
        fpath.remove(filePath)
    }

    override fun showPremium() {
        /* if (isPremiumLayoutSelected) {
             premiumLayout.visibility = View.GONE
             isPremiumLayoutSelected = false
         } else {
             premiumLayout.visibility = View.VISIBLE
             isPremiumLayoutSelected = true
         }*/
    }

    override fun setPremium() {
        //service.setIsPremium(true);
        setWithPremium()
        premiumLayout.visibility = View.GONE
        premiumDate = addSevenDayPremium(WorkWithTimeApi.getDateInFormatYMDHMS(Date()))
        showPremiumActivated()
    }

    override fun checkCode(code: String) {
        //проверка кода
        val query = FirebaseDatabase.getInstance().getReference(CODES).orderByChild(CODE).equalTo(code)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(codesSnapshot: DataSnapshot) {
                if (codesSnapshot.childrenCount == 0L) {
                    showWrongCode()
                } else {
                    val userSnapshot = codesSnapshot.children.iterator().next()
                    val count = userSnapshot.child(COUNT).value as Int
                    if (count > 0) {
                        setPremium()

                        val codeId = userSnapshot.key!!

                        val myRef = FirebaseDatabase.getInstance()
                                .getReference(CODES)
                                .child(codeId)
                        val items = HashMap<String, Any>()
                        items[COUNT] = count - 1
                        myRef.updateChildren(items)
                    } else {
                        showOldCode()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    override fun addSevenDayPremium(date: String): String {
        var sysdateLong = WorkWithTimeApi.getMillisecondsStringDateWithSeconds(date)
        //86400000 - day * 7 day
        sysdateLong += (86400000 * 7).toLong()
        return WorkWithTimeApi.getDateInFormatYMDHMS(Date(sysdateLong))
    }

    override fun setWithPremium() {
        noPremiumText.visibility = View.GONE
        premiumText.visibility = View.VISIBLE
        premiumText.isEnabled = false
    }

    override fun goToMyCalendar(status: String, serviceId: String) {
        showAllDone()
        val intent = Intent(this, MyCalendar::class.java)
        intent.putExtra(SERVICE_ID, serviceId)
        intent.putExtra(STATUS_USER_BY_SERVICE, status)

        startActivity(intent)
        finish()
    }

    override fun showAllDone() {
        Toast.makeText(this, "Сервис успешно создан", Toast.LENGTH_SHORT).show()
    }

    override fun showWrongCode() {
        Toast.makeText(this, "Неверно введен код", Toast.LENGTH_SHORT).show()
    }

    override fun showOldCode() {
        Toast.makeText(this, "Код больше не действителен", Toast.LENGTH_SHORT).show()
    }

    override fun showPremiumActivated() {
        Toast.makeText(this, "Премиум активирован", Toast.LENGTH_SHORT).show()
    }

    override fun showMoreTenImages() {
        Toast.makeText(this, "Должно быть меньше 10 фотографий", Toast.LENGTH_SHORT).show()
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

        private const val MAX_COUNT_OF_IMAGES = 10
    }


}