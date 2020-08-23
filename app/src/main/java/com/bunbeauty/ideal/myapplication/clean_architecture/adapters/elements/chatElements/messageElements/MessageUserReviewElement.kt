package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements

import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter
import kotlinx.android.synthetic.main.element_message.view.*

class MessageUserReviewElement(
    private val messagesPresenter: MessagesPresenter,
    message: Message,
    view: View
) : MessageElement(message, view) {

    override fun setVisibility(message: Message, view: View) {
        if (message.ownerId == User.getMyId()) {
            view.rateMessageElementButton.visibility = View.VISIBLE
            view.rateMessageElementButton.setOnClickListener {
                messagesPresenter.goToCreationComment(message)
            }
        }
    }

}