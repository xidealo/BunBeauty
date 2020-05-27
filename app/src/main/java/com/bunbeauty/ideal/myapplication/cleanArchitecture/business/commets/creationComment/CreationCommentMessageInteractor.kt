package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.UpdateMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.MessageRepository

class CreationCommentMessageInteractor(
    private val messageRepository: MessageRepository,
    private val intent: Intent
) :
    ICreationCommentMessageInteractor, UpdateMessageCallback {
    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback


    override fun checkMessage(
        rating: Double,
        review: String,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        val message = intent.getSerializableExtra(Message.MESSAGE) as Message
        if (message.orderId.isEmpty()) {
            creationCommentPresenterCallback.createUserComment(rating, review)
        } else {
            creationCommentPresenterCallback.getOrderForServiceComment(message, rating, review)
        }
    }

    override fun updateUserCommentMessage(
        userComment: UserComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        val message = intent.getSerializableExtra(Message.MESSAGE) as Message
        message.type = Message.TEXT_MESSAGE_STATUS
        message.message = "Я ставлю оценку ${userComment.rating}, потому что ${userComment.review}"
        messageRepository.update(message, this)
    }

    override fun updateServiceCommentMessage(
        serviceComment: ServiceComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        val message = intent.getSerializableExtra(Message.MESSAGE) as Message
        message.type = Message.TEXT_MESSAGE_STATUS
        message.message =
            "Я ставлю оценку ${serviceComment.rating}, потому что ${serviceComment.review}"
        messageRepository.update(message, this)
    }


    override fun returnUpdatedCallback(obj: Message) {
        creationCommentPresenterCallback.showCommentCreated(obj)
    }

}