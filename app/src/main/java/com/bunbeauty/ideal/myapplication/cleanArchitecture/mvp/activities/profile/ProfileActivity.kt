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
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.CreationServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing.EditProfileActivity
import com.bunbeauty.ideal.myapplication.fragments.SwitcherElement
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.bunbeauty.ideal.myapplication.other.ISwitcher
import com.bunbeauty.ideal.myapplication.reviews.Comments
import com.bunbeauty.ideal.myapplication.subscriptions.Subscribers
import com.squareup.picasso.Picasso
import java.util.*
import javax.inject.Inject

class ProfileActivity : MvpAppCompatActivity(), View.OnClickListener, ProfileView,
    ITopPanel, IBottomPanel,  ISwitcher {

    private lateinit var cityText: TextView
    private lateinit var phoneText: TextView
    private lateinit var withoutRatingText: TextView
    private lateinit var subscribersText: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var addServicesBtn: Button
    private lateinit var subscriptionsBtn: Button
    private lateinit var dialogsBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var ratingLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var avatarImage: ImageView
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var serviceRecyclerView: RecyclerView

    private lateinit var switcherFragment: SwitcherElement

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

        init()
        createBottomPanel(supportFragmentManager)
        createSwitcher()
        profilePresenter.initFCM()
        profilePresenter.createProfileScreen()
    }

    private fun init() {
        avatarImage = findViewById(R.id.avatarProfileImage)
        withoutRatingText = findViewById(R.id.withoutRatingProfileText)
        ratingBar = findViewById(R.id.profileRatingBar)
        progressBar = findViewById(R.id.loadingProfileProgressBar)
        ratingLayout = findViewById(R.id.ratingProfileLayout)
        addServicesBtn = findViewById(R.id.addServicesProfileBtn)
        subscriptionsBtn = findViewById(R.id.subscriptionsProfileBtn)
        dialogsBtn = findViewById(R.id.dialogsProfileBtn)
        mainLayout = findViewById(R.id.mainProfileLayout)
        orderRecyclerView = findViewById(R.id.ordersProfileRecycleView)
        serviceRecyclerView = findViewById(R.id.servicesProfileRecyclerView)
        cityText = findViewById(R.id.cityProfileText)
        phoneText = findViewById(R.id.phoneProfileText)
        subscribersText = findViewById(R.id.subscribersProfileText)

        orderRecyclerView.layoutManager = LinearLayoutManager(this)
        serviceRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addServicesProfileBtn -> goToCreationService()
            R.id.subscriptionsProfileBtn -> goToSubscribers()
/*
            R.id.ratingProfileLayout -> goToComments(profilePresenter.getOwnerId())
*/
        }
    }

    private fun createSwitcher() {
        switcherFragment = SwitcherElement("Записи", "Услуги")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.switcherProfileLayout, switcherFragment)
        transaction.commit()
    }

    override fun showProfileInfo(name: String, city: String, phone: String) {
        cityText.text = city
        phoneText.text = phone
    }

    override fun showMyProfileView() {
        orderRecyclerView.visibility = View.VISIBLE
        addServicesBtn.setOnClickListener(this)
        subscriptionsBtn.setOnClickListener(this)
    }

    override fun showAlienProfileView() {
        serviceRecyclerView.visibility = View.VISIBLE
    }

    override fun createTopPanelForMyProfile(userName: String) {
        createTopPanel(userName, ButtonTask.EDIT, supportFragmentManager)
    }

    override fun createTopPanelForOtherProfile(userName: String) {
        createTopPanel(userName, ButtonTask.NONE, supportFragmentManager)
    }

    override fun showUserServices(serviceList: List<Service>, user: User) {
        val serviceAdapter = ServiceProfileAdapter(serviceList as ArrayList<Service>, user)
        serviceRecyclerView.adapter = serviceAdapter
    }

    override fun hideSubscriptions() {
        subscriptionsBtn.visibility = View.GONE
    }

    override fun showDialogs() {
        dialogsBtn.visibility = View.VISIBLE
    }

    override fun hideDialogs() {
        dialogsBtn.visibility = View.GONE
    }

    override fun showAddService() {
        addServicesBtn.visibility = View.VISIBLE
    }

    override fun hideAddService() {
        addServicesBtn.visibility = View.INVISIBLE
    }

    override fun showWithoutRating() {
        withoutRatingText.visibility = View.VISIBLE
        ratingBar.visibility = View.GONE
    }

    override fun showRating(rating: Float) {
        ratingBar.visibility = View.VISIBLE
        withoutRatingText.visibility = View.GONE
        ratingBar.rating = rating
        ratingLayout.setOnClickListener(this)
    }

    override fun showAvatar(photoLink: String) {
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
    override fun showSubscriptions(subscriptionsCount: Long) {
        if (subscriptionsCount > 0L) {
            subscriptionsBtn.text = "Подписки: $subscriptionsCount"
            subscriptionsBtn.visibility = View.VISIBLE
        }
    }

    override fun showSwitcher() {
        switcherFragment.showSwitcherElement()
    }

    override fun hideSwitcher() {
        switcherFragment.hideSwitcherElement()
    }

    @SuppressLint("SetTextI18n")
    override fun showSubscribers(subscribersCount: Long) {
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


    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
        mainLayout.visibility = View.INVISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
    }

    override fun firstSwitcherAct() {
        hideAddService()
        serviceRecyclerView.visibility = View.GONE
        orderRecyclerView.visibility = View.VISIBLE
    }

    override fun secondSwitcherAct() {
        showAddService()
        orderRecyclerView.visibility = View.GONE
        serviceRecyclerView.visibility = View.VISIBLE
    }

    override fun iconClick() {
        // if isOwner - goToEdit
        // else - subscribe

        val intent = Intent(this, EditProfileActivity::class.java)
        //intent.putExtra(User.USER, editableEntity as User)
        this.startActivity(intent)
    }

    private fun goToCreationService() {
        val intent = Intent(this, CreationServiceActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun goToSubscribers() {
        val intent = Intent(this, Subscribers::class.java)
        intent.putExtra(STATUS, SUBSCRIPTIONS)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun goToComments(ownerId: String?) {
        val intent = Intent(this, Comments::class.java)
        intent.putExtra(SERVICE_OWNER_ID, ownerId)
        //intent.putExtra(User.COUNT_OF_RATES, getCountOfRates())
        intent.putExtra(TYPE, REVIEW_FOR_USER)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    companion object {
        private val TAG = "DBInf"
        private const val REVIEW_FOR_USER = "review for user"
        private const val SUBSCRIPTIONS = "подписки"
        private const val SERVICE_OWNER_ID = "service owner id"
        private const val TYPE = "type"
        private const val STATUS = "status"
    }

}