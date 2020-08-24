package com.bunbeauty.ideal.myapplication.clean_architecture.business.sessions

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.sessions.SessionsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.InsertOrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IOrderRepository

class SessionsOrderInteractor(private val orderRepository: IOrderRepository) : InsertOrderCallback {

    private lateinit var sessionsPresenterCallback: SessionsPresenterCallback

    fun makeAppointment(
        sessionsPresenterCallback: SessionsPresenterCallback,
        service: Service,
        session: Session
    ) {
        this.sessionsPresenterCallback = sessionsPresenterCallback

        val order = Order(
            clientId = User.getMyId(),
            masterId = service.userId,
            serviceId = service.id,
            serviceName = service.name,
            session = session
        )
        orderRepository.insert(order, this)
    }

    override fun returnCreatedCallback(order: Order) {
        sessionsPresenterCallback.showMadeAppointment()
        sessionsPresenterCallback.updateSchedule(order)
        sessionsPresenterCallback.sendMessages(order)
    }

}