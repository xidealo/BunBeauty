package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentPagerAdapter
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ProfilePagerAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.CustomViewPager
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.ScheduleActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat.MessagesActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing.EditProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.subscriptions.SubscriptionsActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.profile.OrdersFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.profile.ServicesFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.squareup.picasso.Picasso
import javax.inject.Inject

class ProfileActivity : MvpAppCompatActivity(), View.OnClickListener, ProfileView,
    ITopPanel, IBottomPanel, TabLayout.OnTabSelectedListener {

    private lateinit var avatarImage: ImageView

    private lateinit var fullNameText: TextView
    private lateinit var cityText: TextView

    private lateinit var subscribersText: TextView

    private lateinit var ratingLayout: LinearLayout
    private lateinit var ratingBar: RatingBar
    private lateinit var ratingText: TextView

    private lateinit var progressBar: ProgressBar

    private lateinit var dialogsBtn: FloatingActionButton
    private lateinit var subscribeBtn: FloatingActionButton

    private lateinit var subscriptionsBtn: FloatingActionButton
    private lateinit var scheduleBtn: FloatingActionButton

    private lateinit var ordersFragment: OrdersFragment
    private lateinit var servicesFragment: ServicesFragment
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: CustomViewPager

    override var bottomNavigationContext: Context = this
    override lateinit var bottomPanel: BottomNavigationView

    @Inject
    lateinit var profileUserInteractor: ProfileUserInteractor

    @Inject
    lateinit var profileServiceInteractor: ProfileServiceInteractor

    @Inject
    lateinit var profileDialogInteractor: ProfileDialogInteractor

    @Inject
    lateinit var profileSubscriptionInteractor: ProfileSubscriptionInteractor

    @Inject
    lateinit var profileSubscriberInteractor: ProfileSubscriberInteractor

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
            profileDialogInteractor,
            profileSubscriptionInteractor,
            profileSubscriberInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        init()
        createTopPanel(" ", ButtonTask.EDIT, supportFragmentManager)
        profilePresenter.initFCM()
        profilePresenter.getProfileOwner()
    }

    override fun onResume() {
        super.onResume()
        profilePresenter.updateBottomPanel()
    }

    private fun init() {
        avatarImage = findViewById(R.id.avatarProfileImage)
        fullNameText = findViewById(R.id.fullNameProfileText)
        cityText = findViewById(R.id.cityProfileText)
        subscribersText = findViewById(R.id.subscribersProfileText)

        ratingBar = findViewById(R.id.profileRatingBar)
        ratingLayout = findViewById(R.id.ratingProfileLayout)
        ratingText = findViewById(R.id.ratingProfileText)

        subscribeBtn = findViewById(R.id.subscribeProfileBtn)
        subscribeBtn.setOnClickListener(this)
        dialogsBtn = findViewById(R.id.dialogsProfileBtn)
        dialogsBtn.setOnClickListener(this)
        subscriptionsBtn = findViewById(R.id.subscriptionsProfileBtn)
        subscriptionsBtn.setOnClickListener(this)
        scheduleBtn = findViewById(R.id.scheduleProfileBtn)
        scheduleBtn.setOnClickListener(this)

        ordersFragment = OrdersFragment()
        servicesFragment = ServicesFragment()
        tabLayout = findViewById(R.id.profileTabLayout)
        viewPager = findViewById(R.id.profileViewPager)
        viewPager.adapter =
            ProfilePagerAdapter(
                supportFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                ordersFragment,
                servicesFragment
            )
        tabLayout.addOnTabSelectedListener(this)
        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))

        progressBar = findViewById(R.id.loadingProfileProgressBar)
    }

    override fun showProfileInfo(name: String, surname: String, city: String) {
        fullNameText.text = "$name $surname"
        cityText.text = city
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

    override fun showRating(rating: Float, countOfRates: Long) {
        ratingBar.rating = rating
        ratingLayout.setOnClickListener(this)
        ratingText.text = "$rating ($countOfRates)"
    }

    override fun setServiceAdapter(services: List<Service>, user: User) {
        servicesFragment.setAdapter(services, user)
    }

    override fun showBottomPanel(selectedItemId: Int) {
        initBottomPanel(selectedItemId)
    }

    override fun showUpdatedBottomPanel(selectedItemId: Int) {
        updateBottomPanel(selectedItemId)
    }

    override fun showCountOfSubscriber(count: Long) {
        subscribersText.text = "Подписчики: $count"
        subscribersText.visibility = View.VISIBLE
    }

    override fun showOrders() {
        viewPager.currentItem = ORDERS_INDEX
    }

    override fun showServices() {
        viewPager.currentItem = SERVICES_INDEX
    }

    override fun showTabLayout() {
        tabLayout.visibility = View.VISIBLE
    }

    override fun hideTabLayout() {
        tabLayout.visibility = View.GONE
    }

    override fun disableSwipe() {
        viewPager.isEnable = false
    }

    override fun showCreateServiceButton() {
        servicesFragment.showCreateButton()
    }

    override fun hideCreateServiceButton() {
        servicesFragment.hideCreateButton()
    }

    override fun showDialogsButton() {
        dialogsBtn.visibility = View.VISIBLE
    }

    override fun hideDialogsButton() {
        dialogsBtn.visibility = View.GONE
    }

    override fun showSubscribeButton() {
        subscribeBtn.visibility = View.VISIBLE
    }

    override fun hideSubscribeButton() {
        subscribeBtn.visibility = View.GONE
    }

    override fun invisibleSubscribeButton() {
        subscribeBtn.visibility = View.INVISIBLE
    }

    override fun showSubscriptionsButton() {
        subscriptionsBtn.visibility = View.VISIBLE
    }

    override fun hideSubscriptionsButton() {
        subscriptionsBtn.visibility = View.GONE
    }

    override fun showScheduleButton() {
        scheduleBtn.visibility = View.VISIBLE
    }

    override fun hideScheduleButton() {
        scheduleBtn.visibility = View.GONE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.subscribeProfileBtn -> profilePresenter.subscribe()
            R.id.dialogsProfileBtn -> profilePresenter.goToDialog()

            R.id.scheduleProfileBtn -> goToSchedule()
            R.id.subscriptionsProfileBtn -> profilePresenter.goToSubscriptions()

            //R.id.ratingProfileLayout -> goToComments(profilePresenter.getOwnerId())
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPager.currentItem = tab.position
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun showUserServices(serviceList: List<Service>, user: User) {
        servicesFragment.updateAdapter()
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
        viewPager.visibility = View.INVISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
        viewPager.visibility = View.VISIBLE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun iconClick() {
        goToEditProfile(profilePresenter.getCurrentUser())
        //profilePresenter.checkIconClick()
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

    private fun goToEditProfile(user: User) {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra(User.USER, user)
        startActivityForResult(intent, REQUEST_EDIT_PROFILE)
    }

    override fun goToDialog(dialog: Dialog) {
        val intent = Intent(this, MessagesActivity::class.java)
        intent.putExtra(Dialog.COMPANION_DIALOG, dialog)
        //TODO(refactor)
        val myDialog = Dialog()
        myDialog.ownerId = dialog.user.id
        myDialog.id = dialog.id
        intent.putExtra(Dialog.DIALOG, myDialog)

        intent.putExtra(User.USER, profilePresenter.getCurrentUser())
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun showSubscribed() {
        subscribeBtn.setImageResource(R.drawable.icon_unsubscribe_24dp)
    }

    override fun showUnsubscribed() {
        subscribeBtn.setImageResource(R.drawable.icon_subscribe_24dp)
    }

    private fun goToSchedule() {
        val intent = Intent(this, ScheduleActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun goToSubscriptions(user: User) {
        val intent = Intent(this, SubscriptionsActivity::class.java)
        intent.putExtra(User.USER, user)
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
        private const val ORDERS_INDEX = 0
        private const val SERVICES_INDEX = 1

        private const val REVIEW_FOR_USER = "review for user"
        private const val SUBSCRIPTIONS = "подписки"
        private const val SERVICE_OWNER_ID = "service owner id"
        private const val TYPE = "type"
        private const val STATUS = "status"
        private const val REQUEST_EDIT_PROFILE = 1
    }

}