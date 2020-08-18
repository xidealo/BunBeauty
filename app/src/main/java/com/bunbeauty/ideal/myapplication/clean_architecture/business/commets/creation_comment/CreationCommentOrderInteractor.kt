package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment

import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment.iCreationComment.ICreationCommentOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.OrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IOrderRepository

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