package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentPagerAdapter
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ProfilePagerAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.CustomViewPager
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.ScheduleActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat.MessagesActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments.UserCommentsActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing.EditProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.subscriptions.SubscriptionsActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.profile.OrdersFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.profile.ServicesFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import javax.inject.Inject

class ProfileActivity : MvpAppCompatActivity(), View.OnClickListener, ProfileView,
    ITopPanel, IBottomPanel, TabLayout.OnTabSelectedListener {

    private lateinit var ratingLayout: LinearLayout

    private lateinit var ordersFragment: OrdersFragment
    private lateinit var servicesFragment: ServicesFragment
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: CustomViewPager

    override var panelContext: Activity = this

    //const
    companion object {
        private const val REQUEST_EDIT_PROFILE = 1
        private const val ORDERS_INDEX = 0
        private const val SERVICES_INDEX = 1
    }

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
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
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
        profilePresenter.initFCM()
        profilePresenter.getProfileOwner()
    }

    override fun onResume() {
        super.onResume()
        profilePresenter.updateBottomPanel()
    }

    private fun init() {
        ratingLayout = findViewById(R.id.ratingProfileLayout)
        ratingProfileLayout.setOnClickListener(this)
        subscribeProfileBtn.setOnClickListener(this)
        dialogsProfileBtn.setOnClickListener(this)
        subscriptionsProfileBtn.setOnClickListener(this)
        scheduleProfileBtn.setOnClickListener(this)
        profileRatingBar.setOnClickListener(this)
        ratingProfileText.setOnClickListener(this)

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
    }

    override fun showProfileInfo(name: String, surname: String, city: String) {
        fullNameProfileText.text = "$name $surname"
        cityProfileText.text = city
    }

    override fun showAvatar(photoLink: String) {
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        Picasso.get()
            .load(photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarProfileImage)
    }

    override fun showCountOfSubscriber(count: Long) {
        subscribersProfileText.text = "Подписчики: $count"
        subscribersProfileText.visibility = View.VISIBLE
    }

    override fun showRating(rating: Float, countOfRates: Long) {
        profileRatingBar.rating = rating
        ratingLayout.setOnClickListener(this)
        ratingProfileText.text = "$rating ($countOfRates)"
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

    override fun showTopPanelWithEditIcon() {
        initTopPanel(ButtonTask.EDIT)
    }

    override fun showEmptyTopPanel() {
        initTopPanel(ButtonTask.NONE)
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
        dialogsProfileBtn.visibility = View.VISIBLE
    }

    override fun hideDialogsButton() {
        dialogsProfileBtn.visibility = View.GONE
    }

    override fun showSubscribeButton() {
        subscribeProfileBtn.visibility = View.VISIBLE
    }

    override fun hideSubscribeButton() {
        subscribeProfileBtn.visibility = View.GONE
    }

    override fun showSubscriptionsButton() {
        subscriptionsProfileBtn.visibility = View.VISIBLE
    }

    override fun hideSubscriptionsButton() {
        subscriptionsProfileBtn.visibility = View.GONE
    }

    override fun showScheduleButton() {
        scheduleProfileBtn.visibility = View.VISIBLE
    }

    override fun hideScheduleButton() {
        scheduleProfileBtn.visibility = View.GONE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.subscribeProfileBtn -> profilePresenter.subscribe()
            R.id.dialogsProfileBtn -> profilePresenter.goToDialog()
            R.id.scheduleProfileBtn -> goToSchedule()
            R.id.subscriptionsProfileBtn -> profilePresenter.goToSubscriptions()
            R.id.ratingProfileLayout -> goToComments(profilePresenter.getCacheOwner())
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
        loadingProfileProgressBar.visibility = View.VISIBLE
        viewPager.visibility = View.INVISIBLE
    }

    override fun hideProgress() {
        loadingProfileProgressBar.visibility = View.GONE
        viewPager.visibility = View.VISIBLE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun actionClick() {
        goToEditProfile(profilePresenter.getCacheOwner())
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

    override fun goToMessages(dialog: Dialog, companionDialog: Dialog) {
        val intent = Intent(this, MessagesActivity::class.java)
        intent.putExtra(Dialog.DIALOG, dialog)
        intent.putExtra(Dialog.COMPANION_DIALOG, companionDialog)
        intent.putExtra(User.USER, profilePresenter.getCacheOwner())
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun showSubscribed() {
        subscribeProfileBtn.setImageResource(R.drawable.icon_heart)
    }

    override fun showUnsubscribed() {
        subscribeProfileBtn.setImageResource(R.drawable.icon_heart_outline)
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

    private fun goToComments(user: User) {
        val intent = Intent(this, UserCommentsActivity::class.java)
        intent.putExtra(User.USER, user)
        startActivity(intent)
    }

}