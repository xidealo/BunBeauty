package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentPagerAdapter
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ProfileOrderAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ProfilePagerAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ProfileServiceAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.PhotoSliderActivity
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_service.*
import javax.inject.Inject

class ProfileActivity : MvpAppCompatActivity(), ProfileView, ITopPanel, IBottomPanel,
    TabLayout.OnTabSelectedListener {

    override var panelContext: Activity = this

    private lateinit var ordersFragment: OrdersFragment
    private lateinit var servicesFragment: ServicesFragment

    @Inject
    lateinit var profileServiceAdapter: ProfileServiceAdapter

    @Inject
    lateinit var profileOrderAdapter: ProfileOrderAdapter

    @Inject
    lateinit var profileUserInteractor: IProfileUserInteractor

    @Inject
    lateinit var profileServiceInteractor: IProfileServiceInteractor

    @Inject
    lateinit var profileOrderInteractor: IProfileOrderInteractor

    @Inject
    lateinit var profileDialogInteractor: IProfileDialogInteractor

    @Inject
    lateinit var profileSubscriptionInteractor: IProfileSubscriptionInteractor

    @Inject
    lateinit var profileSubscriberInteractor: IProfileSubscriberInteractor

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): ProfilePresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)

        return ProfilePresenter(
            profileUserInteractor,
            profileServiceInteractor,
            profileOrderInteractor,
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

    override fun onStart() {
        super.onStart()
        profilePresenter.updateMyProfileServices()
    }

    override fun onResume() {
        super.onResume()
        profilePresenter.updateBottomPanel()
    }

    private fun init() {
        ratingProfileLayout.setOnClickListener {
            goToComments(profilePresenter.getCacheOwner())
        }
        subscribeProfileBtn.setOnClickListener {
            profilePresenter.subscribe()
        }
        dialogsProfileBtn.setOnClickListener {
            profilePresenter.goToDialog()
        }
        subscriptionsProfileBtn.setOnClickListener {
            profilePresenter.goToSubscriptions()
        }
        scheduleProfileBtn.setOnClickListener {
            goToSchedule()
        }

        avatarProfileImage.setOnClickListener {
            openPhoto()
        }

        ordersFragment = OrdersFragment(profileOrderAdapter)
        servicesFragment = ServicesFragment(profileServiceAdapter)
        profileViewPager.adapter =
            ProfilePagerAdapter(
                supportFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                ordersFragment,
                servicesFragment
            )
        profileTabLayout.addOnTabSelectedListener(this)
        profileViewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(profileTabLayout))
    }

    private fun openPhoto() {
        val intent = Intent(this, PhotoSliderActivity::class.java).apply {
            putParcelableArrayListExtra(
                Photo.PHOTO,
                arrayListOf(Photo(link = profilePresenter.getCacheOwner().photoLink))
            )
            putExtra(Photo.LINK, profilePresenter.getCacheOwner().photoLink)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
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
        ratingProfileText.text = "$rating ($countOfRates)"
    }

    override fun showServiceList(serviceList: List<Service>) {
        servicesFragment.updateServiceList(serviceList)
    }

    override fun showOrderList(orderList: List<Order>) {
        ordersFragment.updateOrderList(orderList)
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
        profileViewPager.currentItem = ORDERS_INDEX
    }

    override fun showServices() {
        profileViewPager.currentItem = SERVICES_INDEX
    }

    override fun showTabLayout() {
        profileTabLayout.visibility = View.VISIBLE
    }

    override fun hideTabLayout() {
        profileTabLayout.visibility = View.GONE
    }

    override fun disableSwipe() {
        profileViewPager.isEnable = false
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


    override fun onTabSelected(tab: TabLayout.Tab) {
        profileViewPager.currentItem = tab.position
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun showProgress() {
        loadingProfileProgressBar.visibility = View.VISIBLE
        profileViewPager.visibility = View.INVISIBLE
    }

    override fun hideProgress() {
        loadingProfileProgressBar.visibility = View.GONE
        profileViewPager.visibility = View.VISIBLE
    }

    override fun showMessage(message: String) {
        Snackbar.make(profileMainLayout, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
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

    companion object {
        private const val REQUEST_EDIT_PROFILE = 1
        private const val ORDERS_INDEX = 0
        private const val SERVICES_INDEX = 1
    }
}