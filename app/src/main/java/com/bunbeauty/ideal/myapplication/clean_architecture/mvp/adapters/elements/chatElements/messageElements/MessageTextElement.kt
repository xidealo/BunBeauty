package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.chatElements.messageElements

import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import kotlinx.android.synthetic.main.element_message.view.*

class MessageTextElement(
    message: Message,
    view: View
) : MessageElement(message, view) {

    override fun setButtonVisibility(message: Message, view: View, buttonText: String) {
        view.element_message_btn_action.visibility = View.GONE
    }

}