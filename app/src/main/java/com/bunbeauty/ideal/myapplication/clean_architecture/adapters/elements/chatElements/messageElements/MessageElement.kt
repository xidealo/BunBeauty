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
import java.util.*

abstract class MessageElement(
    private val messagesPresenter: MessagesPresenter
) {
    protected lateinit var message: Message
    protected lateinit var messageTextMessageElementText: TextView
    protected lateinit var messageTimeMessageElementText: TextView
    protected lateinit var messageMessageElementLayout: LinearLayout
    protected lateinit var mainLayoutMessageElementLayout: LinearLayout
    protected lateinit var rateMessageElementButton: MaterialButton

    fun createElement(view: View) {
        messageTextMessageElementText = view.findViewById(R.id.messageTextMessageElementText)
        messageTimeMessageElementText = view.findViewById(R.id.messageTimeMessageElementText)
        messageMessageElementLayout = view.findViewById(R.id.messageMessageElementLayout)
        mainLayoutMessageElementLayout = view.findViewById(R.id.mainLayoutMessageElementLayout)
        rateMessageElementButton = view.findViewById(R.id.rateMessageElementButton)
    }

    open fun setVisibility(message: Message) {}

    fun setData(message: Message) {
        this.message = message
        messageTextMessageElementText.text = message.message
        messageTimeMessageElementText.text =
            WorkWithTimeApi.getDateInFormatYMDHMS(Date(message.time)).substring(11, 16)
    }

    open fun setIsMyMessage(message: Message) {
        if (message.ownerId == User.getMyId()) {
            messageMessageElementLayout.gravity = Gravity.END
        } else {
            messageMessageElementLayout.gravity = Gravity.START
            messagesPresenter.updateCheckedDialog()
        }
    }
}
