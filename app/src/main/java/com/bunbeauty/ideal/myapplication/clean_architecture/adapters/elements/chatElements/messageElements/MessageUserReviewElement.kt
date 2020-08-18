package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements

import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter

class MessageUserReviewElement(
    private val messagesPresenter: MessagesPresenter
) : MessageElement(messagesPresenter) {

    override fun setVisibility(message: Message) {
        if (message.ownerId == User.getMyId()){
            rateMessageElementButton.visibility = View.VISIBLE
            rateMessageElementButton.setOnClickListener {
                messagesPresenter.goToCreationComment(message)
            }
        }
    }

}