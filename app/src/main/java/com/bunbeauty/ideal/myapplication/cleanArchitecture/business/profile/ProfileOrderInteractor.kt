package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileOrderInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.OrdersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IOrderRepository

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