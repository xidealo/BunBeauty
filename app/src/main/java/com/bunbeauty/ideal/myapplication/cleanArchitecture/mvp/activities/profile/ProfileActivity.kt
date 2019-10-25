package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.adapters.OrderAdapter
import com.bunbeauty.ideal.myapplication.adapters.ServiceProfileAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.AddingServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.bunbeauty.ideal.myapplication.fragments.SwitcherElement
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.bunbeauty.ideal.myapplication.helpApi.SubscriptionsApi
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi
import com.bunbeauty.ideal.myapplication.other.ISwitcher
import com.bunbeauty.ideal.myapplication.reviews.Comments
import com.bunbeauty.ideal.myapplication.subscriptions.Subscribers
import com.squareup.picasso.Picasso
import java.util.*
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
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var serviceRecyclerView: RecyclerView
    private lateinit var addServicesBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var subscriptionsLayout: LinearLayout
    private lateinit var database: SQLiteDatabase

    @Inject
    lateinit var profileInteractor: ProfileInteractor

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter

    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var userDao: UserDao
    @Inject
    lateinit var userFirebaseApi: UserFirebaseApi

    @Inject
    lateinit var serviceRepository: UserRepository
    @Inject
    lateinit var serviceDao: ServiceDao
    @Inject
    lateinit var serviceFirebaseApi: UserFirebaseApi

    @ProvidePresenter
    internal fun provideProfilePresenter(): ProfilePresenter {
        DaggerAppComponent.builder()
                .appModule(AppModule(application, intent))
                .build()
                .inject(this)
        return ProfilePresenter(profileInteractor)
    }

    private val countOfSubscribers = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        initView()

        profilePresenter.initFCM()
        profilePresenter.showProfileView()
    }

    override fun onResume() {
        super.onResume()
        orderList.clear()
        createPanels()

        profilePresenter.updateProfileData()
        onProgress()
    }

    private fun initView() {
        subscriptionsLayout = findViewById(R.id.subscriptionsProfileLayout)
        subscriptionsText = findViewById(R.id.subscriptionsProfileText)
        avatarImage = findViewById(R.id.avatarProfileImage)
        withoutRatingText = findViewById(R.id.withoutRatingProfileText)
        ratingBar = findViewById(R.id.ratingBarProfile)
        progressBar = findViewById(R.id.profileProgressBar)
        ratingForUserLayout = findViewById(R.id.ratingProfileLayout)
        addServicesBtn = findViewById(R.id.addServicesProfileBtn)
        mainLayout = findViewById(R.id.mainProfileLayout)
        orderRecyclerView = findViewById(R.id.ordersProfileRecycleView)
        serviceRecyclerView = findViewById(R.id.servicesProfileRecyclerView)
        nameText = findViewById(R.id.nameProfileText)
        cityText = findViewById(R.id.cityProfileText)
        phoneText = findViewById(R.id.phoneProfileText)
        subscribersText = findViewById(R.id.subscribersProfileText)

        orderList = ArrayList()

        val layoutManager = LinearLayoutManager(this)
        orderRecyclerView.layoutManager = layoutManager

        val layoutManagerSecond = LinearLayoutManager(this)
        serviceRecyclerView.layoutManager = layoutManagerSecond

        val dbHelper = DBHelper(this)
        database = dbHelper.readableDatabase
        workWithLocalStorageApi = WorkWithLocalStorageApi(database)

        manager = supportFragmentManager
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addServicesProfileBtn -> goToAddService(this)

            R.id.subscriptionsProfileLayout -> goToSubscribers(this)

            R.id.ratingProfileLayout -> goToUserComments(this, profilePresenter.getOwnerId())

            else -> {
            }
        }
    }

    override fun showUserInfo(user: User) {
        showProfileText(user.name, user.city, user.phone)
        createRatingBar(user.rating)
        showAvatar(user.photoLink)

        showView()
    }

    private fun showProfileText(name: String, city: String, phone: String) {
        nameText.text = name
        cityText.text = city
        phoneText.text = phone
    }

    private fun createRatingBar(rating: Float) {
        if (rating == 0f) {
            setWithoutRating()
        } else {
            addRatingToScreen(rating)
        }
    }

    private fun showAvatar(photoLink: String) {
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        Picasso.get()
                .load(photoLink)
                .resize(width, height)
                .centerCrop()
                .transform(CircularTransformation())
                .into(avatarImage)
    }

    private fun showView() {
        progressBar.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
    }

    override fun showUserServices(services: List<Service>) {
        showServicesList(services)
    }

    private fun getCountOfRates(): String {
        //получаем имя, фамилию и город пользователя по его id
        val sqlQuery = ("SELECT "
                + DBHelper.KEY_COUNT_OF_RATES_USERS
                + " FROM "
                + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE "
                + DBHelper.KEY_ID + " = ?")
        val userCursor = database.rawQuery(sqlQuery, arrayOf(profileInteractor.getOwnerId()))

        if (userCursor.moveToFirst()) {
            val indexCountOfRates = userCursor.getColumnIndex(DBHelper.KEY_COUNT_OF_RATES_USERS)

            countOfRates = userCursor.getString(indexCountOfRates)
            return countOfRates
        }
        userCursor.close()
        return ""
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

    //подгрузка сервисов на serviceList
    private fun showServicesList(serviceList: List<Service>) {
        val serviceAdapter = ServiceProfileAdapter(serviceList.size, serviceList as ArrayList<Service>)
        serviceRecyclerView.adapter = serviceAdapter
        offProgress()
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
                    /*order.id = cursor.getString(indexServiceId)
                    order.orderName = cursor.getString(indexServiceName)
                    order.orderDate = cursor.getString(indexDate)
                    order.orderTime = cursor.getString(indexTime)*/
                    orderList.add(order)
                    visibleCount++
                    //пока в курсоре есть строки и есть новые записи
                } while (cursor.moveToPrevious() && cursorCount > visibleCount)
            }
        }

        val orderAdapter = OrderAdapter(orderList.size, orderList)
        orderRecyclerView.adapter = orderAdapter
        cursor.close()
    }

    //wtf VALENTOS
    fun checkSubscription(): Boolean {
        val sqlQuery = ("SELECT * FROM "
                + DBHelper.TABLE_SUBSCRIBERS
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ? AND "
                + DBHelper.KEY_WORKER_ID + " = ?")

        val cursor = database.rawQuery(sqlQuery, arrayOf(profileInteractor.getUserId(), profileInteractor.getOwnerId()))
        return if (cursor.moveToFirst()) {
            cursor.close()
            true
        } else {
            cursor.close()
            false
        }
    }

    override fun showMyProfileView() {
        createSwitcher()
        orderRecyclerView.visibility = View.VISIBLE
        addServicesBtn.setOnClickListener(this)
        subscriptionsLayout.setOnClickListener(this)
    }

    override fun showAlienProfileView() {
        //hideView()
        serviceRecyclerView.visibility = View.VISIBLE
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
        orderRecyclerView.visibility = View.GONE
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


    private fun onProgress() {
        progressBar.visibility = View.VISIBLE
        mainLayout.visibility = View.INVISIBLE
    }

    private fun offProgress() {
        progressBar.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
    }

    override fun firstSwitcherAct() {
        serviceRecyclerView.visibility = View.GONE
        orderRecyclerView.visibility = View.VISIBLE
        addServicesBtn.visibility = View.INVISIBLE
    }

    override fun secondSwitcherAct() {
        addServicesBtn.visibility = View.VISIBLE
        orderRecyclerView.visibility = View.GONE
        serviceRecyclerView.visibility = View.VISIBLE
    }


    private fun goToAddService(activity: Activity) {
        val intent = Intent(activity, AddingServiceActivity::class.java)
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