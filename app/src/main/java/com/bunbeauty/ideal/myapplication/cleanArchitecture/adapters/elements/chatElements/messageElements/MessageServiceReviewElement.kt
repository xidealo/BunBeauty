package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.chatElements.messageElements

import android.view.View
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.MessagesPresenter

class MessageServiceReviewElement(
    private val messagesPresenter: MessagesPresenter
) : MessageElement(messagesPresenter) {
    override fun setVisibility(message: Message) {
        if (message.ownerId == User.getMyId()) {
            rateMessageElementButton.visibility = View.VISIBLE
            rateMessageElementButton.setOnClickListener {
                messagesPresenter.goToCreationComment(message)
            }
        }
    }
}