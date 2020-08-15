package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ProfileView: MvpView {

    fun showProfileInfo(name: String, surname: String, city: String)
    fun showAvatar(photoLink: String)
    fun showCountOfSubscriber(count: Long)
    fun showRating(rating: Float, countOfRates: Long)
    fun showServiceList(serviceList: List<Service>)
    fun showOrderList(orderList: List<Order>)
    fun showBottomPanel(selectedItemId: Int = -1)
    fun showUpdatedBottomPanel(selectedItemId: Int)
    fun showTopPanelWithEditIcon()
    fun showEmptyTopPanel()
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
    fun showSubscriptionsButton()
    fun hideSubscriptionsButton()
    fun showScheduleButton()
    fun hideScheduleButton()

    fun showSubscribed()
    fun showUnsubscribed()

    fun showMessage(message: String)
    fun showProgress()
    fun hideProgress()

    fun goToMessages(dialog: Dialog, companionDialog: Dialog)
    fun goToSubscriptions(user: User)
}