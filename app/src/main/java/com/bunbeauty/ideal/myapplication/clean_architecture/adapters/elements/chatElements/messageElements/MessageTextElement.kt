package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements

import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter
import kotlinx.android.synthetic.main.element_message.view.*

class MessageTextElement(
    message: Message,
    view: View
) : MessageElement(message, view) {

    override fun setVisibility(message: Message, view: View) {
        view.rateMessageElementButton.visibility = View.GONE
    }

}