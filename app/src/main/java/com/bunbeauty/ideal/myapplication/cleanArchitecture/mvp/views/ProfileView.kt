package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ProfileView: MvpView {

    fun showProfileInfo(name: String, surname: String, city: String)
    fun showAvatar(photoLink: String)
    fun showRating(rating: Float, countOfRates: Long)
    fun setServiceAdapter(services: List<Service>, user: User)

    fun showBottomPanel(selectedItemId: Int = -1)
    fun showUpdatedBottomPanel(selectedItemId: Int)
    fun showCountOfSubscriber(count: Long)
    fun showOrders()
    fun showServices()
    fun showTabLayout()
    fun hideTabLayout()
    fun disableSwipe()
    fun showCreateServiceButton()
    fun hideCreateServiceButton()
    fun showDialogsButton()
    fun hideDialogsButton()
    fun showSubscribeButton()
    fun hideSubscribeButton()
    fun invisibleSubscribeButton()
    fun showSubscriptionsButton()
    fun hideSubscriptionsButton()
    fun showScheduleButton()
    fun hideScheduleButton()

    fun showUserServices(serviceList: List<Service>, user: User)

    fun showSubscribed()
    fun showUnsubscribed()

    fun showMessage(message: String)
    fun showProgress()
    fun hideProgress()

    fun goToDialog(dialog: Dialog)
    fun goToSubscriptions(user: User)
}