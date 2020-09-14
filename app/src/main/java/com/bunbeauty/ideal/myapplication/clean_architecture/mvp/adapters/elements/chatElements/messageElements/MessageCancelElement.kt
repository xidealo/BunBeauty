package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.chatElements.messageElements

import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter
import kotlinx.android.synthetic.main.element_message.view.*
import java.util.*

class MessageCancelElement(
    private val messagesPresenter: MessagesPresenter,
    message: Message,
    view: View
) : MessageElement(message, view) {
    override fun setButtonVisibility(message: Message, view: View, buttonText: String) {
        if (message.ownerId == User.getMyId() && message.finishOrderTime > Date().time) {
            super.setButtonVisibility(message, view, buttonText)
            view.element_message_btn_action.visibility = View.VISIBLE
            view.element_message_btn_action.setOnClickListener {
                messagesPresenter.cancelOrder(message)
            }
        }else{
            view.element_message_btn_action.visibility = View.GONE
        }
    }
}