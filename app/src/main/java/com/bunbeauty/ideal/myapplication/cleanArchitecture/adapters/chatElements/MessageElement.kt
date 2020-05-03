package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.chatElements

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User


class MessageElement(
    private val view: View
) {
    private lateinit var message: Message
    private lateinit var messageTextMessageElementText: TextView
    private lateinit var mainLayoutMessageElementLayout: LinearLayout

    fun createElement(message: Message) {
        this.message = message
        onViewCreated(view)
        setData(message)
    }

    private fun onViewCreated(view: View) {
        messageTextMessageElementText = view.findViewById(R.id.messageTextMessageElementText)
        mainLayoutMessageElementLayout = view.findViewById(R.id.mainLayoutMessageElementLayout)
    }

    private fun setData(message: Message) {
        messageTextMessageElementText.text = message.message

        if (message.userId == User.getMyId()) {
            messageTextMessageElementText.setBackgroundColor(Color.parseColor("#f6db40"))
        }
    }
}
