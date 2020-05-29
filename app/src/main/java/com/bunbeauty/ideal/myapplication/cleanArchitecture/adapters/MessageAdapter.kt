package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.MessageAdapter.MessageViewHolder
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.chatElements.messageElements.MessageServiceReviewElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.chatElements.messageElements.MessageTextElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.chatElements.messageElements.MessageUserReviewElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.MessagesPresenter

class MessageAdapter(
    private val messageList: List<Message>,
    private val messagesPresenter: MessagesPresenter
) : RecyclerView.Adapter<MessageViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MessageViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.fragment_message
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)
        return MessageViewHolder(view, context)
    }

    override fun onBindViewHolder(
        messageViewHolder: MessageViewHolder,
        index: Int
    ) {
        messageViewHolder.bind(messageList[index])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class MessageViewHolder(private val view: View, private val context: Context) :
        ViewHolder(view) {

        fun bind(message: Message) {

            when (message.type) {
                Message.TEXT_MESSAGE_STATUS -> {
                    val messageTextElement = MessageTextElement(messagesPresenter)
                    messageTextElement.createElement(view)
                    messageTextElement.setIsMyMessage(message)
                    messageTextElement.setVisibility(message)
                    messageTextElement.setData(message)
                }

                Message.SERVICE_REVIEW_MESSAGE_STATUS -> {
                    val messageTextElement =
                        MessageServiceReviewElement(messagesPresenter, context)
                    messageTextElement.createElement(view)
                    messageTextElement.setIsMyMessage(message)
                    messageTextElement.setVisibility(message)
                    messageTextElement.setData(message)
                }

                Message.USER_REVIEW_MESSAGE_STATUS -> {
                    val messageUserReviewElement =
                        MessageUserReviewElement(messagesPresenter, context)
                    messageUserReviewElement.createElement(view)
                    messageUserReviewElement.setIsMyMessage(message)
                    messageUserReviewElement.setVisibility(message)
                    messageUserReviewElement.setData(message)
                }
            }
        }
    }

}