package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.service

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.createService.MyCalendar
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments.PremiumElementFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IEditableActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ServiceView
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.other.IPremium
import com.bunbeauty.ideal.myapplication.reviews.Comments
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import java.text.DecimalFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import javax.inject.Inject

class ServiceActivity : MvpAppCompatActivity(), View.OnClickListener, ServiceView, IEditableActivity, IPremium {

    private lateinit var mainScroll: ScrollView
    private lateinit var costText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var addressText: TextView
    private lateinit var ratingText: TextView
    private lateinit var countOfRatesText: TextView
    private lateinit var withoutRatingText: TextView
    private lateinit var premiumText: TextView
    private lateinit var noPremiumText: TextView
    private lateinit var imageFeedLayout: LinearLayout
    private lateinit var premiumLayout: LinearLayout
    private lateinit var topPanelLayout: LinearLayout
    private lateinit var ratingLayout: LinearLayout
    private lateinit var ratingBar: RatingBar
    private lateinit var progressBar: ProgressBar

    private lateinit var userId: String
    private lateinit var serviceId: String
    private lateinit var ownerId: String
    private lateinit var serviceName: String
    private lateinit var countOfRatesForComments: String
    private lateinit var premiumDate: String

    private lateinit var service: Service
    private lateinit var status: String
    private var isMyService: Boolean = false
    private var isPremiumLayoutSelected: Boolean = false
    private lateinit var dbHelper: DBHelper
    private lateinit var database: SQLiteDatabase

    @Inject
    lateinit var serviceInteractor: ServiceInteractor

    @InjectPresenter
    lateinit var servicePresenter: ServicePresenter

    @ProvidePresenter
    internal fun provideServicePresenter(): ServicePresenter {
        DaggerAppComponent.builder()
                .appModule(AppModule(application, intent))
                .build()
                .inject(this)
        return ServicePresenter(serviceInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.guest_service)

        init()
    }

    private fun init() {
        val manager = supportFragmentManager

        mainScroll = findViewById(R.id.guestServiceMainScroll)
        costText = findViewById(R.id.costGuestServiceText)
        descriptionText = findViewById(R.id.descriptionGuestServiceText)
        addressText = findViewById(R.id.addressGuestServiceText)
        ratingText = findViewById(R.id.avgRatingGuestServiceElementText)
        countOfRatesText = findViewById(R.id.countOfRatesGuestServiceElementText)
        withoutRatingText = findViewById(R.id.withoutRatingText)
        premiumText = findViewById(R.id.yesPremiumGuestServiceText)
        noPremiumText = findViewById(R.id.noPremiumGuestServiceText)
        ratingLayout = findViewById(R.id.ratingGuestServiceLayout)
        premiumLayout = findViewById(R.id.premiumGuestServiceLayout)
        imageFeedLayout = findViewById(R.id.feedGuestServiceLayout)
        topPanelLayout = findViewById(R.id.headerGuestServiceLayout)
        ratingBar = findViewById(R.id.ratingBarGuestServiceRatingBar)
        progressBar = findViewById(R.id.progressBarGuestService)

        val editScheduleBtn = findViewById<Button>(R.id.editScheduleGuestServiceBtn)
        val premiumIconLayout = findViewById<LinearLayout>(R.id.premiumIconGuestServiceLayout)

        servicePresenter.getService()
        onProgress()

        /*dbHelper = DBHelper(this)
        database = dbHelper.readableDatabase

        userId = servicePresenter.getUserId()
        servicePresenter.isMyService()

        serviceId = intent.getStringExtra(SERVICE_ID)
        ownerId = ""//getOwnerId()
        // мой сервис или нет?
        isMyService = userId == ownerId

        //убрана панель премиума
        isPremiumLayoutSelected = false
        if (isMyService) {
            status = WORKER
            editScheduleBtn.text = "Редактировать расписание"
            premiumText!!.setOnClickListener(this)
            noPremiumText!!.setOnClickListener(this)
            premiumText!!.setOnClickListener(this)

            val premiumElementFragment = PremiumElementFragment()
            val transaction = manager.beginTransaction()
            transaction.add(R.id.premiumGuestServiceLayout, premiumElementFragment)
            transaction.commit()
        } else {
            status = USER
            editScheduleBtn.text = "Расписание"
            premiumIconLayout.visibility = View.GONE
        }
        editScheduleBtn.setOnClickListener(this)*/
    }

    private fun onProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun offProgress() {
        progressBar.visibility = View.GONE
        mainScroll.visibility = View.VISIBLE
    }

    override fun showServiceInfo(service: Service) {
        costText.text = service.cost
        descriptionText.text = service.description
        addressText.text = service.address
        ratingText.text = service.rating.toString()
        countOfRatesText.text = service.countOfRates.toString()
        //withoutRatingText.text = service.cost

        offProgress()
    }

    private fun getOwnerId() {
        /*val sqlQuery = ("SELECT *"
                + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = ? ")
        val cursor = database!!.rawQuery(sqlQuery, arrayOf<String>(serviceId))
        if (cursor.moveToFirst()) {
            val indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID)
            ownerId = cursor.getString(indexUserId)
        }
        return ownerId*/
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editScheduleGuestServiceBtn -> if (status == WORKER) {
                // если мой сервис, я - воркер
                // сразу идём редактировать расписание
                goToMyCalendar(WORKER)
            } else {
                // если не мой сервис, я - юзер
                // проверяем какие дни мне доступны
                checkScheduleAndGoToCalendar()
            }
            R.id.noPremiumGuestServiceText -> showPremium()

            R.id.yesPremiumGuestServiceText -> showPremium()

            R.id.ratingGuestServiceLayout -> goToComments()
            else -> {
            }
        }
    }

    private fun getInfoAboutService(serviceId: String?) {
        // все осервисе, оценка, количество оценок
        val sqlQuery = ("SELECT *"
                + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = ? ")

        val cursor = database!!.rawQuery(sqlQuery, arrayOf<String>(serviceId!!))

        if (cursor.moveToFirst()) {
            val indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES)
            val indexMinCost = cursor.getColumnIndex(DBHelper.KEY_MIN_COST_SERVICES)
            val indexAddress = cursor.getColumnIndex(DBHelper.KEY_ADDRESS_SERVICES)
            val indexIsPremium = cursor.getColumnIndex(DBHelper.KEY_IS_PREMIUM_SERVICES)
            val indexServiceRating = cursor.getColumnIndex(DBHelper.KEY_RATING_SERVICES)
            val indexCountOfRates = cursor.getColumnIndex(DBHelper.KEY_COUNT_OF_RATES_SERVICES)
            val indexDescription = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION_SERVICES)
            val serviceRating = java.lang.Float.valueOf(cursor.getString(indexServiceRating))
            premiumDate = cursor.getString(indexIsPremium)
            val isPremium = WorkWithTimeApi.checkPremium(premiumDate)
            val countOfRates = java.lang.Long.valueOf(cursor.getString(indexCountOfRates))

            val service = Service()
            service.cost = cursor.getString(indexMinCost)
            service.address = cursor.getString(indexAddress)
            service.description = cursor.getString(indexDescription)
            service.name = cursor.getString(indexName)
            service.rating = serviceRating
            service.countOfRates = countOfRates
            //service.setPremiumDate(isPremium);

            setGuestService(service)
        }
        cursor.close()
    }

    private fun setGuestService(service: Service) {

        serviceName = service.name
        costText!!.text = "Цена от: " + service.cost + "р"
        addressText!!.text = "Адрес: " + service.address
        descriptionText!!.text = service.description

        /* if (service.getIsPremium()) {
            setWithPremium();
        }
*/
        countOfRatesForComments = service.countOfRates.toString()
        createRatingBar(service.rating, service.countOfRates)
        createPhotoFeed(serviceId)
    }

    private fun buildPanels() {
        val panelBuilder = PanelBuilder()
        val manager = supportFragmentManager
        topPanelLayout!!.removeAllViews()

        //panelBuilder.buildHeader(manager, getServiceName(), R.id.headerGuestServiceLayout, isMyService, serviceId, ownerId)
        panelBuilder.buildFooter(manager, R.id.footerGuestServiceLayout)

    }

    private fun checkScheduleAndGoToCalendar() {
        if (WorkWithLocalStorageApi.hasAvailableTime(serviceId, userId, dbHelper!!.readableDatabase)) {
            goToMyCalendar(USER)
        } else {
            attentionThisScheduleIsEmpty()
        }
    }

    private fun createPhotoFeed(serviceId: String?) {

        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)

        val database = dbHelper!!.writableDatabase
        //получаем ссылку на фото по id владельца
        val sqlQuery = ("SELECT "
                + DBHelper.KEY_PHOTO_LINK_PHOTOS
                + " FROM "
                + DBHelper.TABLE_PHOTOS
                + " WHERE "
                + DBHelper.KEY_OWNER_ID_PHOTOS + " = ?")
        val cursor = database.rawQuery(sqlQuery, arrayOf<String>(serviceId!!))

        if (cursor.moveToFirst()) {
            do {
                val indexPhotoLink = cursor.getColumnIndex(DBHelper.KEY_PHOTO_LINK_PHOTOS)

                val photoLink = cursor.getString(indexPhotoLink)

                val serviceImage = ImageView(applicationContext)

                val params = LinearLayout.LayoutParams(
                        width,
                        height)
                params.setMargins(15, 15, 15, 15)
                serviceImage.layoutParams = params
                serviceImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                imageFeedLayout!!.addView(serviceImage)

                //установка аватарки
                Picasso.get()
                        .load(photoLink)
                        .resize(width, height)
                        .centerCrop()
                        .into(serviceImage)
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    override fun onResume() {
        super.onResume()

       /* if (!serviceIdsFirstSetGS.contains(serviceId)) {
            loadServiceData()
            serviceIdsFirstSetGS.add(serviceId!!)
        } else {
            //получаем данные о сервисе
            getInfoAboutService(serviceId)
        }

        buildPanels()
        imageFeedLayout!!.removeAllViews()
        createPhotoFeed(serviceId)*/
    }

    private fun attentionThisScheduleIsEmpty() {
        Toast.makeText(
                this,
                "Пользователь еще не написал расписание к этому сервису.",
                Toast.LENGTH_SHORT).show()
    }

    private fun createRatingBar(avgRating: Float, countOfRates: Long) {
        Log.d(TAG, "createRatingBar: $countOfRates")
        if (countOfRates > 0) {
            Log.d(TAG, "createRatingBar: IN")
            countOfRatesText!!.visibility = View.VISIBLE
            ratingText!!.visibility = View.VISIBLE
            ratingBar!!.visibility = View.VISIBLE

            countOfRatesText!!.text = "($countOfRates оценок)"

            //приводим цифры к виду, чтобы было 2 число после запятой
            val avgRatingWithFormat = DecimalFormat("#0.00").format(avgRating.toDouble())
            ratingText!!.text = avgRatingWithFormat

            ratingBar!!.rating = avgRating
            ratingLayout!!.setOnClickListener(this)
        } else {
            Log.d(TAG, "createRatingBar:  SET WITHOUT")
            setWithoutRating()
        }

        mainScroll!!.visibility = View.VISIBLE
        progressBar!!.visibility = View.GONE
    }

    private fun showPremium() {
        if (isPremiumLayoutSelected) {
            premiumLayout!!.visibility = View.GONE
            isPremiumLayoutSelected = false
        } else {
            premiumLayout!!.visibility = View.VISIBLE
            isPremiumLayoutSelected = true
        }
    }

    private fun setWithoutRating() {
        withoutRatingText!!.visibility = View.VISIBLE
    }

    private fun setWithPremium() {
        noPremiumText!!.visibility = View.GONE
        premiumText!!.visibility = View.VISIBLE
    }

    override fun setPremium() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(User.USERS)
                .child(servicePresenter.getUserId())
                .child(Service.SERVICES)
                .child(serviceId!!)
                .child(IS_PREMIUM)
        val newPremiumDate = addSevenDayPremium(premiumDate)
        myRef.setValue(newPremiumDate)

        setWithPremium()
        premiumLayout!!.visibility = View.GONE
        attentionPremiumActivated()
        updatePremiumLocalStorage(serviceId!!, newPremiumDate)
    }

    private fun updatePremiumLocalStorage(serviceId: String, premiumDate: String) {
        val contentValues = ContentValues()
        contentValues.put(DBHelper.KEY_IS_PREMIUM_SERVICES, premiumDate)
        //update
        database!!.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                DBHelper.KEY_ID + " = ?",
                arrayOf(serviceId))
    }

    override fun checkCode(code: String) {
        //проверка кода
        val query = FirebaseDatabase.getInstance().getReference(CODES).orderByChild(CODE).equalTo(code)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(codesSnapshot: DataSnapshot) {
                if (codesSnapshot.childrenCount == 0L) {
                    attentionWrongCode()
                } else {
                    val userSnapshot = codesSnapshot.children.iterator().next()
                    val count = userSnapshot.child(COUNT).getValue(Int::class.javaPrimitiveType!!)!!
                    if (count > 0) {
                        setPremium()

                        val codeId = userSnapshot.key

                        val myRef = FirebaseDatabase.getInstance()
                                .getReference(CODES)
                                .child(codeId!!)
                        val items = HashMap<String, Any>()
                        items[COUNT] = count - 1
                        myRef.updateChildren(items)
                    } else {
                        attentionOldCode()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun addSevenDayPremium(date: String?): String {
        var sysdateLong = WorkWithTimeApi.getMillisecondsStringDateWithSeconds(date)
        if (sysdateLong < WorkWithTimeApi.getSysdateLong()) {
            sysdateLong = WorkWithTimeApi.getSysdateLong()
        }
        //86400000 - day * 7 day
        sysdateLong += (86400000 * 7).toLong()
        return WorkWithTimeApi.getDateInFormatYMDHMS(Date(sysdateLong))
    }

    private fun attentionWrongCode() {
        Toast.makeText(this, "Неверно введен код", Toast.LENGTH_SHORT).show()
    }

    private fun attentionOldCode() {
        Toast.makeText(this, "Код больше не действителен", Toast.LENGTH_SHORT).show()
    }

    private fun attentionPremiumActivated() {
        Toast.makeText(this, "Премиум активирован", Toast.LENGTH_LONG).show()
    }

    override fun goToEditing(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun goToMyCalendar(status: String) {
        val intent = Intent(this, MyCalendar::class.java)
        intent.putExtra(SERVICE_ID, serviceId)
        intent.putExtra(STATUS_USER_BY_SERVICE, status)

        startActivity(intent)
    }

    private fun goToComments() {
        val intent = Intent(this, Comments::class.java)
        intent.putExtra(SERVICE_ID, serviceId)
        intent.putExtra(TYPE, REVIEW_FOR_SERVICE)
        intent.putExtra(SERVICE_OWNER_ID, ownerId)
        intent.putExtra(Service.COUNT_OF_RATES, countOfRatesForComments)
        startActivity(intent)
    }

    companion object {

        private val TAG = "DBInf"

        private val WORKER = "worker"
        private val USER = "user"
        private val SERVICE_ID = "service id"
        private val TYPE = "type"
        private val IS_PREMIUM = "is premium"
        private val SERVICE_OWNER_ID = "service owner id"
        private val CODES = "codes"
        private val CODE = "code"
        private val WORKING_DAYS = "working days"
        private val WORKING_TIME = "working time"
        private val DATE = "date"
        private val ORDERS = "orders"
        private val COUNT = "count"
        private val PHOTOS = "photos"

        private val STATUS_USER_BY_SERVICE = "status UserCreateService"

        private val REVIEW_FOR_SERVICE = "review for service"
    }

}