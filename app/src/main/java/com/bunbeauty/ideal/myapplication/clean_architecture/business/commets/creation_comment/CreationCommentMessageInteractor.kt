package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment.i_creation_comment.ICreationCommentMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message.UpdateMessageCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.MessageRepository

class CreationCommentMessageInteractor(
    private val messageRepository: MessageRepository,
    private val intent: Intent
) :
    ICreationCommentMessageInteractor, UpdateMessageCallback {
    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback

    override fun checkMessage(
        rating: Float,
        review: String,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        val message = intent.getSerializableExtra(Message.MESSAGE) as Message
        if (message.type == Message.USER_REVIEW_STATUS) {
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
        updateMyMessage(
            intent.getSerializableExtra(Dialog.DIALOG) as Dialog,
            "Я ставлю оценку ${userComment.rating}, потому что ${userComment.review}"
        )
        updateCompanionMessage(
            intent.getSerializableExtra(Dialog.DIALOG) as Dialog,
            "Я ставлю оценку ${userComment.rating}, потому что ${userComment.review}"
        )
    }

    override fun updateServiceCommentMessage(
        serviceComment: ServiceComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        updateMyMessage(
            intent.getSerializableExtra(Dialog.DIALOG) as Dialog,
            "Я ставлю оценку услуге ${serviceComment.serviceName} ${serviceComment.rating}, потому что ${serviceComment.review}"
        )
        updateCompanionMessage(
            intent.getSerializableExtra(Dialog.DIALOG) as Dialog,
            "Я ставлю оценку ${serviceComment.serviceName} ${serviceComment.rating}, потому что ${serviceComment.review}"
        )
    }

    private fun updateMyMessage(dialog: Dialog, messageText: String) {
        val message = (intent.getSerializableExtra(Message.MESSAGE) as Message).copy()
        message.userId = dialog.user.id
        message.type = Message.TEXT_STATUS
        message.message = messageText
        messageRepository.update(message, this)
    }

    private fun updateCompanionMessage(dialog: Dialog, messageText: String) {
        val companionMessage = (intent.getSerializableExtra(Message.MESSAGE) as Message).copy()
        companionMessage.dialogId = dialog.user.id
        companionMessage.userId = dialog.id
        companionMessage.type = Message.TEXT_STATUS
        companionMessage.message = messageText
        messageRepository.update(companionMessage, this)
    }

    override fun returnUpdatedCallback(obj: Message) {
        creationCommentPresenterCallback.showCommentCreated(obj)
    }

}