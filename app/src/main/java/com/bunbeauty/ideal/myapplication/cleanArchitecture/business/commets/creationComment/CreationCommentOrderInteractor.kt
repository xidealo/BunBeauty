package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentOrderInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.OrderCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.OrderRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IOrderRepository

class CreationCommentOrderInteractor(private val orderRepository: IOrderRepository) :
    ICreationCommentOrderInteractor, OrderCallback {

    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback

    override fun getOrderById(
        message: Message,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        //orderRepository.getById(message.userId, message.orderId, this)
    }

    override fun returnGottenObject(element: Order?) {
        if (element == null) return

        creationCommentPresenterCallback.createServiceComment(element)
    }

}