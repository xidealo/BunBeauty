package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements

import android.view.Gravity
import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import kotlinx.android.synthetic.main.element_message.view.*
import java.util.*

abstract class MessageElement(
    message: Message,
    view: View
) {

    init {
        view.element_message_tv_message.text = message.message
        view.element_message_tv_time.text =
            WorkWithTimeApi.getDateInFormatYMDHMS(Date(message.time)).substring(11, 16)
    }

    open fun setButtonVisibility(message: Message, view: View, buttonText: String) {
        view.element_message_btn_action.text = buttonText
    }

    open fun setIsMyMessage(message: Message, view: View) {
        if (message.ownerId == User.getMyId()) {
            view.element_message_ll_main.gravity = Gravity.END
        } else {
            view.element_message_ll_main.gravity = Gravity.START
        }
    }
}
