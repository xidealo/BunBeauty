package com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile.iProfile.IProfileOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.OrdersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IOrderRepository
import org.joda.time.DateTime
import org.joda.time.Hours

class ProfileOrderInteractor(private val orderRepository: IOrderRepository) :
    IProfileOrderInteractor, OrdersCallback {

    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    override fun getOrderListByUserId(userId: String, profilePresenterCallback: ProfilePresenterCallback) {
        this.profilePresenterCallback = profilePresenterCallback

        orderRepository.getByUserId(userId, this)
    }

    override fun returnList(orderList: List<Order>) {
        val now = DateTime.now().plusHours(3).toLocalDateTime()
        profilePresenterCallback.showOrderList(
            orderList.filter { // get orders has ended less then 1 hour or has not ended
                val finishSession = DateTime(it.session.finishTime).toLocalDateTime()
                Hours.hoursBetween(finishSession, now).hours < 1
            }
        )
    }
}