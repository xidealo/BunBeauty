package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentPagerAdapter
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.ProfileOrderAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.ProfilePagerAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.ProfileServiceAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.invisible
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.roundSomeSymbols
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.iProfile.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.*
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.PhotoSliderActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.ScheduleActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.chat.MessagesActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.UserCommentsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.editing.EditProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.subscriptions.SubscriptionsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.OrdersFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.ServicesFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.ProfilePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.ProfileView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import javax.inject.Inject

class ProfileActivity : BaseActivity(), ProfileView, TabLayout.OnTabSelectedListener {

    @Inject
    lateinit var ordersFragment: OrdersFragment

    @Inject
    lateinit var servicesFragment: ServicesFragment

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
        buildDagger().inject(this)
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
        profilePresenter.checkProfileToUpdateServices()
        profilePresenter.checkProfileToUpdateOrders()
    }

    private fun init() {
        activity_profile_mcv_rating.setOnClickListener {
            goToComments(profilePresenter.getCacheOwner())
        }
        activity_profile_btn_subscribe.setOnClickListener {
            profilePresenter.subscribe()
        }
        activity_profile_btn_dialogs.setOnClickListener {
            profilePresenter.getDialog()
        }
        activity_profile_btn_subscriptions.setOnClickListener {
            profilePresenter.goToSubscriptions()
        }
        activity_profile_btn_schedule.setOnClickListener {
            goToSchedule()
        }

        activity_profile_iv_avatar.setOnClickListener {
            openPhoto()
        }

        activity_profile_vp.adapter = ProfilePagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
            ordersFragment,
            servicesFragment
        )
        activity_profile_tl_tab.addOnTabSelectedListener(this)
        activity_profile_vp.addOnPageChangeListener(
            TabLayoutOnPageChangeListener(
                activity_profile_tl_tab
            )
        )
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
        activity_profile_tv_full_name.text = "$name $surname"
        activity_profile_tv_city.text = city
    }

    override fun showAvatar(photoLink: String) {
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        Picasso.get()
            .load(photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(activity_profile_iv_avatar)
    }

    override fun showCountOfSubscriber(count: Long) {
        activity_profile_tv_subscribers.text = "Подписчики: $count"
        activity_profile_tv_subscribers.visible()
    }

    override fun showRating(rating: Float, countOfRates: Long) {
        activity_profile_rb_rating.rating = rating
        activity_profile_tv_rating.text = "${rating.roundSomeSymbols(2)} ($countOfRates)"
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
        initTopPanel(buttonTask = ButtonTask.EDIT)
    }

    override fun showEmptyTopPanel() {
        initTopPanel()
    }

    override fun showOrders() {
        activity_profile_vp.currentItem = ORDERS_INDEX
    }

    override fun showServices() {
        activity_profile_vp.currentItem = SERVICES_INDEX
    }

    override fun showTabLayout() {
        activity_profile_tl_tab.visible()
    }

    override fun hideTabLayout() {
        activity_profile_tl_tab.gone()
    }

    override fun disableSwipe() {
        activity_profile_vp.isEnable = false
    }

    override fun showCreateServiceButton() {
        servicesFragment.createServiceButtonVisibility = View.VISIBLE
    }

    override fun hideCreateServiceButton() {
        servicesFragment.createServiceButtonVisibility = View.GONE
    }

    override fun showDialogsButton() {
        activity_profile_btn_dialogs.visible()
    }

    override fun hideDialogsButton() {
        activity_profile_btn_dialogs.gone()
    }

    override fun showSubscribeButton() {
        activity_profile_btn_subscribe.visible()
    }

    override fun hideSubscribeButton() {
        activity_profile_btn_subscribe.gone()
    }

    override fun showSubscriptionsButton() {
        activity_profile_btn_subscriptions.visible()
    }

    override fun hideSubscriptionsButton() {
        activity_profile_btn_subscriptions.gone()
    }

    override fun showScheduleButton() {
        activity_profile_btn_schedule.visible()
    }

    override fun hideScheduleButton() {
        activity_profile_btn_schedule.gone()
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        activity_profile_vp.currentItem = tab.position
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun showProgress() {
        activity_profile_pb_loading.visible()
        activity_profile_vp.invisible()
    }

    override fun hideProgress() {
        activity_profile_pb_loading.gone()
        activity_profile_vp.visible()
    }

    override fun showMessage(message: String) {
        Snackbar.make(activity_profile_cl_main, message, Snackbar.LENGTH_LONG)
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
        overridePendingTransition(0, 0)
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
        activity_profile_btn_subscribe.setImageResource(R.drawable.icon_heart)
    }

    override fun showUnsubscribed() {
        activity_profile_btn_subscribe.setImageResource(R.drawable.icon_heart_outline)
    }

    private fun goToSchedule() {
        val intent = Intent(this, ScheduleActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun goToSubscriptions(user: User) {
        val intent = Intent(this, SubscriptionsActivity::class.java).apply {
            putExtra(User.USER, user)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun goToComments(user: User) {
        val intent = Intent(this, UserCommentsActivity::class.java).apply {
            putExtra(User.USER, user)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    companion object {
        private const val REQUEST_EDIT_PROFILE = 1
        private const val ORDERS_INDEX = 0
        private const val SERVICES_INDEX = 1
    }
}