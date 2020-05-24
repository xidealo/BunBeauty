package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.UpdateMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Comment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.MessageRepository

class CreationCommentMessageInteractor(
    private val messageRepository: MessageRepository,
    private val intent: Intent
) :
    ICreationCommentMessageInteractor, UpdateMessageCallback {
    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback

    override fun updateCommentMessage(
        comment: Comment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        val message = intent.getSerializableExtra(Message.MESSAGE) as Message
        message.type = Message.TEXT_MESSAGE_STATUS
        message.message = "Я ставлю оценку ${comment.rating}, потому что ${comment.review}"
        messageRepository.update(message, this)
    }

    override fun returnUpdatedCallback(obj: Message) {
        creationCommentPresenterCallback.showCommentCreated(obj)
    }

}