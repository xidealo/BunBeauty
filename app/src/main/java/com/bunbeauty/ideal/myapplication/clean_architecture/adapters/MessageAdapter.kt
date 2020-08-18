package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.MessageAdapter.MessageViewHolder
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements.MessageServiceReviewElement
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements.MessageTextElement
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements.MessageUserReviewElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter

class MessageAdapter : RecyclerView.Adapter<MessageViewHolder>() {

    private val messageList: MutableList<Message> = ArrayList()
    private lateinit var messagesPresenter: MessagesPresenter

    fun setData(messageList: List<Message>, messagesPresenter: MessagesPresenter) {
        this.messageList.clear()
        this.messageList.addAll(messageList)
        this.messagesPresenter = messagesPresenter
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MessageViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_message
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
                        MessageServiceReviewElement(messagesPresenter)
                    messageTextElement.createElement(view)
                    messageTextElement.setIsMyMessage(message)
                    messageTextElement.setVisibility(message)
                    messageTextElement.setData(message)
                }

                Message.USER_REVIEW_MESSAGE_STATUS -> {
                    val messageUserReviewElement =
                        MessageUserReviewElement(messagesPresenter)
                    messageUserReviewElement.createElement(view)
                    messageUserReviewElement.setIsMyMessage(message)
                    messageUserReviewElement.setVisibility(message)
                    messageUserReviewElement.setData(message)
                }
            }
        }
    }

}