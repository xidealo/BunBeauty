package com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.profile

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView

import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.adapters.OrderAdapter
import com.bunbeauty.ideal.myapplication.adapters.ServiceProfileAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView
import com.bunbeauty.ideal.myapplication.createService.AddingService
import com.bunbeauty.ideal.myapplication.fragments.SwitcherElement
import com.bunbeauty.ideal.myapplication.helpApi.SubscriptionsApi
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi
import com.bunbeauty.ideal.myapplication.other.ISwitcher
import com.bunbeauty.ideal.myapplication.reviews.Comments
import com.bunbeauty.ideal.myapplication.subscriptions.Subscribers

import java.util.ArrayList
import javax.inject.Inject

class ProfileActivity : MvpAppCompatActivity(), View.OnClickListener, ProfileView, ISwitcher {

    private lateinit var countOfRates: String

    private lateinit var nameText: TextView
    private lateinit var cityText: TextView
    private lateinit var phoneText: TextView
    private lateinit var withoutRatingText: TextView
    private lateinit var subscribersText: TextView
    private lateinit var subscriptionsText: TextView
    private lateinit var ratingBar: RatingBar

    private lateinit var ratingForUserLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout

    private lateinit var workWithLocalStorageApi: WorkWithLocalStorageApi
    private lateinit var avatarImage: ImageView

    private lateinit var manager: FragmentManager

    private lateinit var orderList: ArrayList<Order>
    private lateinit var serviceList: ArrayList<Service>
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewService: RecyclerView
    private lateinit var addServicesBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var subscriptionsLayout: LinearLayout
    private lateinit var database: SQLiteDatabase

    @Inject
    internal lateinit var profileInteractor: ProfileInteractor

    @InjectPresenter
    internal lateinit var profilePresenter: ProfilePresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): ProfilePresenter {
        DaggerAppComponent.builder()
                .appModule(AppModule(application))
                .build()
                .inject(this)
        return ProfilePresenter(profileInteractor)
    }

    private val countOfSubscribers: Long
        get() {
            val sqlQuery = ("SELECT " + DBHelper.KEY_SUBSCRIBERS_COUNT_USERS
                    + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                    + " WHERE " + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID + " = ?")

            val cursor = database.rawQuery(sqlQuery, arrayOf(profileInteractor.getUserId()))
            if (cursor.moveToFirst()) {
                val indexSubscribersCount = cursor.getColumnIndex(DBHelper.KEY_SUBSCRIBERS_COUNT_USERS)
                return java.lang.Long.valueOf(cursor.getString(indexSubscribersCount))
            }
            cursor.close()
            return 0
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        initView()

        profilePresenter.initFCM(intent)
        profilePresenter.showProfile(intent)
    }

    private fun initView() {
        subscriptionsLayout = findViewById(R.id.subscriptionsProfileLayout)
        subscriptionsText = findViewById(R.id.subscriptionsProfileText)
        avatarImage = findViewById(R.id.avatarProfileImage)
        withoutRatingText = findViewById(R.id.withoutRatingProfileText)
        ratingBar = findViewById(R.id.ratingBarProfile)
        progressBar = findViewById(R.id.progressBarProfile)
        ratingForUserLayout = findViewById(R.id.ratingProfileLayout)
        addServicesBtn = findViewById(R.id.addServicesProfileBtn)
        mainLayout = findViewById(R.id.mainLayoutProfile)
        recyclerView = findViewById(R.id.resultsProfileRecycleView)
        recyclerViewService = findViewById(R.id.servicesProfileRecyclerView)
        nameText = findViewById(R.id.nameProfileText)
        cityText = findViewById(R.id.cityProfileText)
        phoneText = findViewById(R.id.phoneProfileText)
        subscribersText = findViewById(R.id.subscribersProfileText)

        serviceList = ArrayList()
        orderList = ArrayList()

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val layoutManagerSecond = LinearLayoutManager(this)
        recyclerViewService.layoutManager = layoutManagerSecond

        val dbHelper = DBHelper(this)
        database = dbHelper.readableDatabase
        workWithLocalStorageApi = WorkWithLocalStorageApi(database)

        manager = supportFragmentManager
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addServicesProfileBtn -> goToAddService(this)

            R.id.subscriptionsProfileLayout -> goToSubscribers(this)

            R.id.ratingProfileLayout -> goToUserComments(this, profilePresenter.getOwnerId(intent))

            else -> {
            }
        }
    }

    override fun showUserInfo(user: User) {
        showProfileText(user.name, user.city, user.phone)
        createRatingBar(user.rating)
        //showAvatar()
    }

    override fun showUserServices(services: List<Service>) {
        updateServicesList(services)
    }

    private fun getCountOfRates(): String {
        //получаем имя, фамилию и город пользователя по его id
        val sqlQuery = ("SELECT "
                + DBHelper.KEY_COUNT_OF_RATES_USERS
                + " FROM "
                + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE "
                + DBHelper.KEY_ID + " = ?")
        val userCursor = database.rawQuery(sqlQuery, arrayOf(profileInteractor.getOwnerId(intent)))

        if (userCursor.moveToFirst()) {
            val indexCountOfRates = userCursor.getColumnIndex(DBHelper.KEY_COUNT_OF_RATES_USERS)

            countOfRates = userCursor.getString(indexCountOfRates)
            return countOfRates
        }
        userCursor.close()
        return ""
    }

    override fun onResume() {
        super.onResume()
        orderList.clear()
        createPanels()

        profilePresenter.updateProfileData(intent)
    }

    private fun showSubscriptions() {
        val subscriptionsCount = SubscriptionsApi.getCountOfSubscriptions(database, profileInteractor.getUserId())
        var subscriptionText = "Подписки"

        if (subscriptionsCount != 0L) {
            subscriptionText += " ($subscriptionsCount)"
        }
        subscriptionsText.text = subscriptionText
    }

    private fun showSubscribers() {
        var subscribersBtnText = "Подписчики:"
        val subscribersCount = countOfSubscribers

        if (subscribersCount != 0L) {
            subscribersBtnText += " $subscribersCount"
        }
        subscribersText.text = subscribersBtnText
    }

    private fun createRatingBar(rating: Float) {
        if (rating == 0f) {
            setWithoutRating()
        } else {
            addRatingToScreen(rating)
        }
    }

    private fun showAvatar() {
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        workWithLocalStorageApi.setPhotoAvatar(profileInteractor.getOwnerId(intent),
                avatarImage,
                width,
                height)
    }

    private fun showProfileText(name: String, city: String, phone: String) {
        nameText.text = name
        cityText.text = city
        phoneText.text = phone
    }

    //подгрузка сервисов на serviceList
    private fun updateServicesList(services: List<Service>) {
        /*serviceList.clear()
        //количество сервисов отображаемых на данный момент(старых)
        val sqlQueryService = ("SELECT "
                + DBHelper.KEY_ID + ", "
                + DBHelper.KEY_NAME_SERVICES + ", "
                + DBHelper.KEY_RATING_SERVICES
                + " FROM "
                + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ? "
                + " AND "
                + DBHelper.KEY_RATING_SERVICES + " IS NOT NULL")

        val cursor = database.rawQuery(sqlQueryService, arrayOf(ownerId))

        if (cursor.moveToFirst()) {
            val indexServiceId = cursor.getColumnIndex(DBHelper.KEY_ID)
            val indexServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES)
            val indexServiceRating = cursor.getColumnIndex(DBHelper.KEY_RATING_SERVICES)
            do {

                val serviceId = cursor.getString(indexServiceId)
                val serviceName = cursor.getString(indexServiceName)
                val serviceRating = java.lang.Float.valueOf(cursor.getString(indexServiceRating))

                val service = Service()
                service.id = serviceId
                service.name = serviceName
                service.averageRating = serviceRating

                serviceList.add(service)
            } while (cursor.moveToNext())
        }*/
        val serviceAdapter = ServiceProfileAdapter(serviceList.size, services as ArrayList<Service>)
        recyclerViewService.adapter = serviceAdapter
        progressBar.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
    }

    //добавляет вновь добавленные записи (обновление ordersList)
    private fun updateOrdersList(userId: String) {
        // количство записей отображаемых на данный момент(старых)
        var visibleCount = orderList.size

        // получаем id сервиса, имя сервиса, дату и время всех записей
        // Из 3-х таблиц: сервисы, рабочие дни, рабочие время
        // Условие: связка таблиц по id сервиса и id рабочего дня; уточняем пользователя по id
        val sqlQuery = ("SELECT "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_NAME_SERVICES + ", "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_DATE_WORKING_DAYS + ", "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_TIME_WORKING_TIME
                + " FROM "
                + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_ORDERS
                + " WHERE "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                + " AND "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " AND "
                + DBHelper.KEY_IS_CANCELED_ORDERS + " = 'false'"
                + " AND "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                + " AND "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? "
                + " AND "
                + " STRFTIME('%s', " + DBHelper.KEY_DATE_WORKING_DAYS
                + ")>=STRFTIME('%s', DATE('now'))")

        val cursor = database.rawQuery(sqlQuery, arrayOf(userId))

        val cursorCount = cursor.count

        //если есть новые записи
        if (cursorCount > visibleCount) {
            //Идём с конца
            if (cursor.moveToLast()) {
                val indexServiceId = cursor.getColumnIndex(DBHelper.KEY_ID)
                val indexServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES)
                val indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS)
                val indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME)

                do {
                    val order = Order()
                    order.orderId = cursor.getString(indexServiceId)
                    order.orderName = cursor.getString(indexServiceName)
                    order.orderDate = cursor.getString(indexDate)
                    order.orderTime = cursor.getString(indexTime)
                    orderList.add(order)
                    visibleCount++
                    //пока в курсоре есть строки и есть новые записи
                } while (cursor.moveToPrevious() && cursorCount > visibleCount)
            }
        }

        val orderAdapter = OrderAdapter(orderList.size, orderList)
        recyclerView.adapter = orderAdapter
        cursor.close()
    }

    //wtf VALENTOS
    fun checkSubscription(): Boolean {
        val sqlQuery = ("SELECT * FROM "
                + DBHelper.TABLE_SUBSCRIBERS
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ? AND "
                + DBHelper.KEY_WORKER_ID + " = ?")

        val cursor = database.rawQuery(sqlQuery, arrayOf(profileInteractor.getUserId(), profileInteractor.getOwnerId(intent)))
        if (cursor.moveToFirst()) {
            cursor.close()
            return true
        } else {
            cursor.close()
            return false
        }
    }

    override fun showMyProfile() {
        recyclerViewService.visibility = View.GONE
        createSwitcher()
        addServicesBtn.setOnClickListener(this)
        subscriptionsLayout.setOnClickListener(this)
    }

    override fun showNotMyProfile() {
        hideView()
        recyclerViewService.visibility = View.VISIBLE
    }

    private fun createSwitcher() {
        val switcherElement = SwitcherElement("Записи", "Услуги")
        val transaction = manager.beginTransaction()
        transaction.add(R.id.switcherProfileLayout, switcherElement)
        transaction.commit()
    }

    private fun hideView() {
        addServicesBtn.visibility = View.GONE
        subscriptionsLayout.visibility = View.INVISIBLE
        subscribersText.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }

    private fun addRatingToScreen(avgRating: Float) {
        ratingBar.visibility = View.VISIBLE
        ratingBar.rating = avgRating
        ratingForUserLayout.setOnClickListener(this)
    }

    private fun setWithoutRating() {
        ratingBar.visibility = View.GONE
        withoutRatingText.visibility = View.VISIBLE
    }

    private fun createPanels() {
        /*val panelBuilder = PanelBuilder(profileInteractor.isMyProfile(intent))
        panelBuilder.buildHeader(manager, "Профиль", R.id.headerProfileLayout)
        panelBuilder.buildFooter(manager, R.id.footerProfileLayout)*/
    }

    override fun firstSwitcherAct() {
        recyclerViewService.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        addServicesBtn.visibility = View.INVISIBLE
    }

    override fun secondSwitcherAct() {
        addServicesBtn.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        recyclerViewService.visibility = View.VISIBLE
    }


    private fun goToAddService(activity: Activity) {
        val intent = Intent(activity, AddingService::class.java)
        activity.startActivity(intent)
    }

    private fun goToSubscribers(activity: Activity) {
        val intent = Intent(activity, Subscribers::class.java)
        intent.putExtra(STATUS, SUBSCRIPTIONS)
        activity.startActivity(intent)
    }

    private fun goToUserComments(activity: Activity, ownerId: String?) {
        val intent = Intent(activity, Comments::class.java)
        intent.putExtra(SERVICE_OWNER_ID, ownerId)
        intent.putExtra(User.COUNT_OF_RATES, getCountOfRates())
        intent.putExtra(TYPE, REVIEW_FOR_USER)
        activity.startActivity(intent)
    }

    companion object {

        private val TAG = "DBInf"
        private val REVIEW_FOR_USER = "review for user"
        private val SUBSCRIPTIONS = "подписки"
        private val SERVICE_OWNER_ID = "service owner id"
        private val TYPE = "type"
        private val STATUS = "status"

        private val userIdsFirstSetProfile = ArrayList<String>()
    }

}