package com.bunbeauty.ideal.myapplication.clean_architecture.business.profile

import com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.iProfile.IProfileOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.OrdersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IOrderRepository

class ProfileOrderInteractor(private val orderRepository: IOrderRepository) :
    IProfileOrderInteractor, OrdersCallback {

    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    override fun getOrderListByUserId(userId: String, profilePresenterCallback: ProfilePresenterCallback) {
        this.profilePresenterCallback = profilePresenterCallback

        orderRepository.getByUserId(userId, this)
    }

    override fun returnList(objects: List<Order>) {
        profilePresenterCallback.showOrderList(objects)
    }
}