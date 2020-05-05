package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile

import android.annotation.SuppressLint
import android.content.Context
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
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ServiceProfileAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.ScheduleActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat.MessagesActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.CreationServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing.EditProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.subscriptions.SubscriptionsActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.bunbeauty.ideal.myapplication.reviews.Comments
import com.google.android.material.button.MaterialButtonToggleGroup
import com.squareup.picasso.Picasso
import javax.inject.Inject

class ProfileActivity : MvpAppCompatActivity(), View.OnClickListener, ProfileView,
    ITopPanel, IBottomPanel, MaterialButtonToggleGroup.OnButtonCheckedListener {

    private lateinit var cityText: TextView
    private lateinit var phoneText: TextView
    private lateinit var withoutRatingText: TextView
    private lateinit var subscribersText: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var progressBar: ProgressBar
    private lateinit var ratingLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var avatarImage: ImageView
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var serviceRecyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceProfileAdapter

    private lateinit var addServicesBtn: Button
    private lateinit var subscriptionsBtn: Button
    private lateinit var dialogsBtn: Button
    private lateinit var subscribeProfileBtn: Button
    private lateinit var scheduleBtn: Button
    private lateinit var ordersBtn: Button
    private lateinit var servicesBtn: Button
    private lateinit var switcher: MaterialButtonToggleGroup

    override var bottomNavigationContext: Context = this

    @Inject
    lateinit var profileUserInteractor: ProfileUserInteractor

    @Inject
    lateinit var profileServiceInteractor: ProfileServiceInteractor

    @Inject
    lateinit var profileDialogInteractor: ProfileDialogInteractor

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): ProfilePresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application, intent))
            .build()
            .inject(this)
        return ProfilePresenter(
            profileUserInteractor,
            profileServiceInteractor,
            profileDialogInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        init()
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
        dialogsBtn.setOnClickListener(this)
        scheduleBtn = findViewById(R.id.scheduleProfileBtn)
        scheduleBtn.setOnClickListener(this)
        subscribeProfileBtn = findViewById(R.id.subscribeProfileBtn)
        subscribeProfileBtn.setOnClickListener(this)
        ordersBtn = findViewById(R.id.ordersProfileBtn)
        servicesBtn = findViewById(R.id.servicesProfileBtn)
        switcher = findViewById(R.id.switcherProfileToggle)
        switcher.addOnButtonCheckedListener(this)

        initBottomPanel(R.id.navigation_profile)

        mainLayout = findViewById(R.id.mainProfileLayout)
        cityText = findViewById(R.id.cityProfileText)
        phoneText = findViewById(R.id.phoneProfileText)
        subscribersText = findViewById(R.id.subscribersProfileText)

        orderRecyclerView = findViewById(R.id.ordersProfileRecycleView)
        serviceRecyclerView = findViewById(R.id.servicesProfileRecyclerView)
        orderRecyclerView.layoutManager = LinearLayoutManager(this)
        serviceRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addServicesProfileBtn -> goToCreationService()
            R.id.subscriptionsProfileBtn -> goToSubscribers()
            R.id.dialogsProfileBtn -> profilePresenter.goToDialog()
            R.id.scheduleProfileBtn -> goToSchedule()

            // R.id.ratingProfileLayout -> goToComments(profilePresenter.getOwnerId())
        }
    }

    override fun onButtonChecked(
        group: MaterialButtonToggleGroup,
        checkedId: Int,
        isChecked: Boolean
    ) {
        when (checkedId) {
            R.id.ordersProfileBtn -> {
                ordersBtn.isEnabled = false
                servicesBtn.isEnabled = true
                showOrders()
            }

            R.id.servicesProfileBtn -> {
                ordersBtn.isEnabled = true
                servicesBtn.isEnabled = false
                showServices()
            }
        }
    }

    override fun showProfileInfo(user: User) {
        cityText.text = user.city
        phoneText.text = user.phone

        serviceAdapter = ServiceProfileAdapter(profilePresenter.getServiceLink(), user)
        serviceRecyclerView.adapter = serviceAdapter
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
        serviceAdapter.notifyDataSetChanged()
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

    override fun showSubscribe() {
        subscribeProfileBtn.visibility = View.VISIBLE
    }

    override fun hideSubscribe() {
        subscribeProfileBtn.visibility = View.GONE
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

    override fun showSwitcher() {
        switcher.visibility = View.VISIBLE
    }

    override fun hideSwitcher() {
        switcher.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    override fun showSubscriptions(subscriptionsCount: Long) {
        if (subscriptionsCount > 0L) {
            subscriptionsBtn.text = "Подписки: $subscriptionsCount"
            subscriptionsBtn.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showSubscribers(subscribersCount: Long) {
        if (subscribersCount > 0L) {
            subscribersText.text = "Подписчики: $subscribersCount"
            subscribersText.visibility = View.VISIBLE
        }
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
        mainLayout.visibility = View.INVISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
    }

    private fun showOrders() {
        hideAddService()
        serviceRecyclerView.visibility = View.GONE
        orderRecyclerView.visibility = View.VISIBLE
    }

    private fun showServices() {
        showAddService()
        orderRecyclerView.visibility = View.GONE
        serviceRecyclerView.visibility = View.VISIBLE
    }

    override fun iconClick() {
        profilePresenter.checkIconClick()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun goToEditProfile(user: User) {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra(User.USER, user)
        startActivityForResult(intent, REQUEST_EDIT_PROFILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null || resultCode != RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_EDIT_PROFILE -> {
                showMessage("Профиль изменен")
                profilePresenter.updateUser(data.getSerializableExtra(User.USER) as User)
            }

        }

    }

    override fun goToDialog(dialog: Dialog) {
        val intent = Intent(this, MessagesActivity::class.java)
    }

    override fun subscribe() {

    }

    private fun goToCreationService() {
        val intent = Intent(this, CreationServiceActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun goToSchedule() {
        val intent = Intent(this, ScheduleActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun goToSubscribers() {
        val intent = Intent(this, SubscriptionsActivity::class.java)
        intent.putExtra(STATUS, SUBSCRIPTIONS)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun goToComments(ownerId: String?) {
        /*val intent = Intent(this, Comments::class.java)
        intent.putExtra(SERVICE_OWNER_ID, ownerId)
        //intent.putExtra(User.COUNT_OF_RATES, getCountOfRates())
        intent.putExtra(TYPE, REVIEW_FOR_USER)
        startActivity(intent)
        overridePendingTransition(0, 0)*/
    }


    companion object {
        private const val TAG = "DBInf"
        private const val REVIEW_FOR_USER = "review for user"
        private const val SUBSCRIPTIONS = "подписки"
        private const val SERVICE_OWNER_ID = "service owner id"
        private const val TYPE = "type"
        private const val STATUS = "status"
        private const val REQUEST_EDIT_PROFILE = 1
    }

}