package com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.*

interface ProfilePresenterCallback {
    fun returnProfileOwner(user: User)
    fun showServiceList(serviceList: List<Service>)
    fun showOrderList(orderList: List<Order>)
    fun showMyProfile(user: User)
    fun showAlienProfile(user: User)
    fun goToMessages(myDialog: Dialog, companionDialog: Dialog)
    fun showSubscribed()
    fun addSubscription(subscriber: Subscriber)
    fun deleteSubscription(subscriber: Subscriber)
    fun showUnsubscribed()
    fun showCountOfSubscriber(count: Long)
    fun getOrderList(userId: String)
    fun getServiceList(userId: String)
    fun showUpdatedBottomPanel(selectedItemId: Int = -1)
    fun updateCountOfSubscribers(subscriber: Int)
}
