package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.element_message.view.*
import java.util.*

abstract class MessageElement(
    message: Message,
    view: View
) {

    init {
        view.messageTextMessageElementText.text = message.message
        view.messageTimeMessageElementText.text =
            WorkWithTimeApi.getDateInFormatYMDHMS(Date(message.time)).substring(11, 16)
    }

    open fun setVisibility(message: Message, view: View) {}

    open fun setIsMyMessage(message: Message, view: View) {
        if (message.ownerId == User.getMyId()) {
            view.messageMessageElementLayout.gravity = Gravity.END
        } else {
            view.messageMessageElementLayout.gravity = Gravity.START
        }
    }
}
