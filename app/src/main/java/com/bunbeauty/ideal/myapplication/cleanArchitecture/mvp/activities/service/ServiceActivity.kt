package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.service

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.createService.MyCalendar
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IEditableActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.BottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.TopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ServiceView
import com.bunbeauty.ideal.myapplication.editing.EditService
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.other.IPremium
import com.bunbeauty.ideal.myapplication.reviews.Comments
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
    private lateinit var onPremiumText: TextView
    private lateinit var offPremiumText: TextView
    private lateinit var imageFeedLayout: LinearLayout
    private lateinit var premiumLayout: LinearLayout
    private lateinit var topPanelLayout: LinearLayout
    private lateinit var ratingLayout: LinearLayout
    private lateinit var ratingBar: RatingBar
    private lateinit var progressBar: ProgressBar

    private lateinit var owner: User
    private lateinit var service: Service

   /* private lateinit var userId: String
    private lateinit var serviceId: String
    private lateinit var ownerId: String
    private lateinit var serviceName: String
    private lateinit var countOfRatesForComments: String
    private lateinit var premiumDate: String

    private lateinit var service: Service
    private lateinit var status: String
    private var isPremiumLayoutSelected: Boolean = false
    private lateinit var dbHelper: DBHelper
    private lateinit var database: SQLiteDatabase*/

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
        setContentView(R.layout.service_activity)

        init()
        showBottomPanel()
    }

    override fun onResume() {
        super.onResume()

        onProgress()
        servicePresenter.getUserAndService()
    }

    private fun init() {
        mainScroll = findViewById(R.id.guestServiceMainScroll)
        costText = findViewById(R.id.costGuestServiceText)
        descriptionText = findViewById(R.id.descriptionGuestServiceText)
        addressText = findViewById(R.id.addressGuestServiceText)
        ratingText = findViewById(R.id.avgRatingGuestServiceElementText)
        countOfRatesText = findViewById(R.id.countOfRatesGuestServiceElementText)
        withoutRatingText = findViewById(R.id.withoutRatingText)
        onPremiumText = findViewById(R.id.onPremiumServiceText)
        offPremiumText = findViewById(R.id.offPremiumServiceText)
        ratingLayout = findViewById(R.id.ratingGuestServiceLayout)
        premiumLayout = findViewById(R.id.premiumServiceLayout)
        imageFeedLayout = findViewById(R.id.feedGuestServiceLayout)
        topPanelLayout = findViewById(R.id.topServiceLayout)
        ratingBar = findViewById(R.id.ratingBarGuestServiceRatingBar)
        progressBar = findViewById(R.id.progressBarGuestService)

        findViewById<Button>(R.id.scheduleServiceBtn).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.premiumServiceLayout).setOnClickListener(this)
    }

    private fun onProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun offProgress() {
        progressBar.visibility = View.GONE
        mainScroll.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    override fun showServiceInfo(owner: User, service: Service) {
        this.owner = owner
        this.service = service

        servicePresenter.setTopPanel(owner.id, owner.photoLink, service.id, service.name)
        costText.text = service.cost + " ₽"
        descriptionText.text = service.description
        addressText.text = service.address
        showRating(service.rating, service.countOfRates)
        servicePresenter.setPremium(owner.id, service.premiumDate)

        offProgress()
    }

    private fun showRating(rating: Float, countOfRates: Long) {
        if (rating > 0) {
            showRatingBar(rating)
            countOfRatesText.text = countOfRates.toString()
        } else {
            showWithoutRating()
        }
    }

    private fun showWithoutRating() {
        withoutRatingText.visibility = View.VISIBLE
        ratingBar.visibility = View.GONE
    }

    private fun showRatingBar(rating: Float) {
        ratingBar.visibility = View.VISIBLE
        ratingBar.rating = rating
        ratingLayout.setOnClickListener(this)
    }

    override fun showBottomPanel() {
        val bottomPanel = BottomPanel()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.bottomServiceLayout, bottomPanel)
        transaction.commit()
    }

    override fun showTopPanelForMyService(serviceId: String, serviceName: String) {
        val topPanel = TopPanel()

        topPanel.title = serviceName
        topPanel.entityId = serviceId

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.topServiceLayout, topPanel)
        transaction.commit()
    }

    override fun showTopPanelForAlienService(serviceName: String, ownerPhotoLink: String, ownerId: String) {
        val topPanel = TopPanel()

        topPanel.title = serviceName
        topPanel.photoLink = ownerPhotoLink
        topPanel.ownerId = ownerId

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.topServiceLayout, topPanel)
        transaction.commit()
    }

    override fun showPremium(isPremium: Boolean) {
        premiumLayout.visibility = View.VISIBLE
        if (isPremium) {
            offPremiumText.visibility = View.GONE
            onPremiumText.visibility = View.VISIBLE
        } else {
            onPremiumText.visibility = View.GONE
            offPremiumText.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.scheduleServiceBtn -> if (servicePresenter.isMyService(owner.id)) {
                // my service, Im master
                goToCalendar(User.MASTER)
            } else {
                // not my service, Im client
                //checkScheduleAndGoToCalendar()
            }

            R.id.offPremiumServiceText -> showPremium()

            R.id.onPremiumServiceText -> showPremium()

            R.id.ratingGuestServiceLayout -> goToComments()
        }
    }

    /*private fun checkScheduleAndGoToCalendar() {
        if (WorkWithLocalStorageApi.hasAvailableTime(serviceId, userId, dbHelper!!.readableDatabase)) {
            goToCalendar(USER)
        } else {
            attentionThisScheduleIsEmpty()
        }
    }*/

    private fun createPhotoFeed(serviceId: String?) {

        /*val width = resources.getDimensionPixelSize(R.dimen.photo_width)
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
        cursor.close()*/
    }

    private fun attentionThisScheduleIsEmpty() {
        Toast.makeText(
                this,
                "Пользователь еще не написал расписание к этому сервису.",
                Toast.LENGTH_SHORT).show()
    }

    private fun showPremium() {
       /* if (isPremiumLayoutSelected) {
            premiumLayout!!.visibility = View.GONE
            isPremiumLayoutSelected = false
        } else {
            premiumLayout!!.visibility = View.VISIBLE
            isPremiumLayoutSelected = true
        }*/
    }

    private fun setWithoutRating() {
        withoutRatingText!!.visibility = View.VISIBLE
    }

    private fun setWithPremium() {
        offPremiumText!!.visibility = View.GONE
        onPremiumText!!.visibility = View.VISIBLE
    }

    override fun setPremium() {
        /*val database = FirebaseDatabase.getInstance()
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
        updatePremiumLocalStorage(serviceId!!, newPremiumDate)*/
    }

    private fun updatePremiumLocalStorage(serviceId: String, premiumDate: String) {
       /* val contentValues = ContentValues()
        contentValues.put(DBHelper.KEY_IS_PREMIUM_SERVICES, premiumDate)
        //update
        database!!.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                DBHelper.KEY_ID + " = ?",
                arrayOf(serviceId))*/
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
        val intent = Intent(this, EditService::class.java)
        intent.putExtra(Service.SERVICE_ID, id)
        startActivity(intent)
    }

    private fun goToCalendar(status: String) {
        val intent = Intent(this, MyCalendar::class.java)
        intent.putExtra(Service.SERVICE_ID, "")
        intent.putExtra(STATUS_USER_BY_SERVICE, status)

        startActivity(intent)
    }

    fun goToOwnerProfile(ownerId: String) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(User.USER_ID, ownerId)
        startActivity(intent)
    }

    private fun goToComments() {
        val intent = Intent(this, Comments::class.java)
        intent.putExtra(Service.SERVICE_ID, "")
        intent.putExtra(TYPE, REVIEW_FOR_SERVICE)
        intent.putExtra(Service.USER_ID, "")
        intent.putExtra(Service.COUNT_OF_RATES, "")
        startActivity(intent)
    }

    companion object {

        private val TAG = "DBInf"

        private val TYPE = "type"
        private val IS_PREMIUM = "is premium"
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