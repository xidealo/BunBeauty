package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.chatElements

import android.view.View
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message


class MessageElement(
    private val view: View
) {
    private lateinit var message: Message
    private lateinit var messageTextMessageElementText: TextView

    fun createElement(message: Message) {
        this.message = message
        onViewCreated(view)
        setData(message)
    }

    private fun onViewCreated(view: View) {
        messageTextMessageElementText = view.findViewById(R.id.messageTextMessageElementText)
    }

    private fun setData(message: Message) {
        messageTextMessageElementText.text = message.message
    }
}
