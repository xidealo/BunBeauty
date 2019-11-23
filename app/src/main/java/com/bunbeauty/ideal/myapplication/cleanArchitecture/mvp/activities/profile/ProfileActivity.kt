package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.adapters.ServiceProfileAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.EditableEntity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.AddingServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IEditableActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.BottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.TopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView
import com.bunbeauty.ideal.myapplication.editing.EditProfile
import com.bunbeauty.ideal.myapplication.fragments.SwitcherElement
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.bunbeauty.ideal.myapplication.other.ISwitcher
import com.bunbeauty.ideal.myapplication.reviews.Comments
import com.bunbeauty.ideal.myapplication.subscriptions.Subscribers
import com.squareup.picasso.Picasso
import java.util.*
import javax.inject.Inject

class ProfileActivity : MvpAppCompatActivity(), View.OnClickListener, ProfileView, ISwitcher,
        IEditableActivity, ITopPanel, IBottomPanel {

    private lateinit var nameText: TextView
    private lateinit var cityText: TextView
    private lateinit var phoneText: TextView
    private lateinit var withoutRatingText: TextView
    private lateinit var subscribersText: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var addServicesBtn: Button
    private lateinit var subscriptionsBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var ratingLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var avatarImage: ImageView
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var serviceRecyclerView: RecyclerView

    private lateinit var profileOwner: User

    @Inject
    lateinit var profileInteractor: ProfileInteractor

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): ProfilePresenter {
        DaggerAppComponent.builder()
                .appModule(AppModule(application, intent))
                .build()
                .inject(this)
        return ProfilePresenter(profileInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        initView()

        createBottomPanel(supportFragmentManager, R.id.bottomProfileLayout)
        profilePresenter.initFCM()
        profilePresenter.showProfileView()
    }

    override fun onResume() {
        super.onResume()

        onProgress()
        profilePresenter.updateProfileData()
    }

    private fun initView() {
        avatarImage = findViewById(R.id.avatarProfileImage)
        withoutRatingText = findViewById(R.id.withoutRatingProfileText)
        ratingBar = findViewById(R.id.profileRatingBar)
        progressBar = findViewById(R.id.profileProgressBar)
        ratingLayout = findViewById(R.id.ratingProfileLayout)
        addServicesBtn = findViewById(R.id.addServicesProfileBtn)
        subscriptionsBtn = findViewById(R.id.subscriptionsProfileBtn)
        mainLayout = findViewById(R.id.mainProfileLayout)
        orderRecyclerView = findViewById(R.id.ordersProfileRecycleView)
        serviceRecyclerView = findViewById(R.id.servicesProfileRecyclerView)
        nameText = findViewById(R.id.nameProfileText)
        cityText = findViewById(R.id.cityProfileText)
        phoneText = findViewById(R.id.phoneProfileText)
        subscribersText = findViewById(R.id.subscribersProfileText)

        val layoutManager = LinearLayoutManager(this)
        orderRecyclerView.layoutManager = layoutManager

        val layoutManagerSecond = LinearLayoutManager(this)
        serviceRecyclerView.layoutManager = layoutManagerSecond
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addServicesProfileBtn -> goToAddService()

            R.id.subscriptionsProfileBtn -> goToSubscribers()

            R.id.ratingProfileLayout -> goToComments(profilePresenter.getOwnerId())

            else -> {}
        }
    }

    override fun showUserInfo(user: User) {
        this.profileOwner = user

        showProfileText(user.name, user.city, user.phone)
        showRating(user.rating)
        showAvatar(user.photoLink)
        if (profilePresenter.isUserOwner()) {
            showSubscribers(user.subscribersCount)
            showSubscriptions(user.subscriptionsCount)
        }

        createTopPanel()
    }

    override fun showUserServices(serviceList: List<Service>) {
        val serviceAdapter = ServiceProfileAdapter(serviceList as ArrayList<Service>, profileOwner)
        serviceRecyclerView.adapter = serviceAdapter
        offProgress()
    }

    private fun showProfileText(name: String, city: String, phone: String) {
        nameText.text = name
        cityText.text = city
        phoneText.text = phone
    }

    private fun showRating(rating: Float) {
        if (rating == 0f) {
            showWithoutRating()
        } else {
            showRatingBar(rating)
        }
    }

    private fun showWithoutRating() {
        withoutRatingText.visibility = View.VISIBLE
    }

    private fun showRatingBar(rating: Float) {
        ratingBar.visibility = View.VISIBLE
        ratingBar.rating = rating
        ratingLayout.setOnClickListener(this)
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

    @SuppressLint("SetTextI18n")
    private fun showSubscriptions(subscriptionsCount: Long) {
        if (subscriptionsCount > 0L) {
            subscriptionsBtn.text = "Подписки: $subscriptionsCount"
        } else {
            subscriptionsBtn.text = "Подписки"
        }
        subscriptionsBtn.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun showSubscribers(subscribersCount: Long) {
        if (subscribersCount > 0L) {
            subscribersText.text = "Подписчики: $subscribersCount"
            subscribersText.visibility = View.VISIBLE
        }
    }

    //добавляет вновь добавленные записи (обновление ordersList)
   /* private fun updateOrdersList(userId: String) {
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
                    *//*order.id = cursor.getString(indexServiceId)
                    order.orderName = cursor.getString(indexServiceName)
                    order.orderDate = cursor.getString(indexDate)
                    order.orderTime = cursor.getString(indexTime)*//*
                    orderList.add(order)
                    visibleCount++
                    //пока в курсоре есть строки и есть новые записи
                } while (cursor.moveToPrevious() && cursorCount > visibleCount)
            }
        }

        val orderAdapter = OrderAdapter(orderList.size, orderList)
        orderRecyclerView.adapter = orderAdapter
        cursor.close()
    }*/

    override fun showMyProfileView() {
        createSwitcher()
        orderRecyclerView.visibility = View.VISIBLE
        addServicesBtn.setOnClickListener(this)
        subscriptionsBtn.setOnClickListener(this)
    }

    override fun showAlienProfileView() {
        serviceRecyclerView.visibility = View.VISIBLE
    }

    private fun createSwitcher() {
        val switcherElement = SwitcherElement("Записи", "Услуги")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.switcherProfileLayout, switcherElement)
        transaction.commit()
    }

    override fun createTopPanel() {
        val topPanel = TopPanel()

        if(profilePresenter.isUserOwner()) {
            topPanel.title = "Профиль"
            topPanel.editableEntity = profileOwner
            topPanel.isEditable = true
        } else {
            topPanel.title = profileOwner.name
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.topProfileLayout, topPanel)
        transaction.commit()
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

    override fun goToEditing(editableEntity: EditableEntity) {
        val intent = Intent(this, EditProfile::class.java)
        intent.putExtra(User.USER, editableEntity as User)
        this.startActivity(intent)
    }

    private fun goToAddService() {
        val intent = Intent(this, AddingServiceActivity::class.java)
        this.startActivity(intent)
    }

    private fun goToSubscribers() {
        val intent = Intent(this, Subscribers::class.java)
        intent.putExtra(STATUS, SUBSCRIPTIONS)
        this.startActivity(intent)
    }

    private fun goToComments(ownerId: String?) {
        val intent = Intent(this, Comments::class.java)
        intent.putExtra(SERVICE_OWNER_ID, ownerId)
        //intent.putExtra(User.COUNT_OF_RATES, getCountOfRates())
        intent.putExtra(TYPE, REVIEW_FOR_USER)
        this.startActivity(intent)
    }

    companion object {

        private val TAG = "DBInf"
        private val REVIEW_FOR_USER = "review for user"
        private val SUBSCRIPTIONS = "подписки"
        private val SERVICE_OWNER_ID = "service owner id"
        private val TYPE = "type"
        private val STATUS = "status"
    }

}