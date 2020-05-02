package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.MessageAdapter.MessageViewHolder
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message

class MessageAdapter(
    private val messageList: List<Message>
) : RecyclerView.Adapter<MessageViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MessageViewHolder {
        // в этом методе создаем ViewHolder
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.message_element
        //Класс, который позволяет создавать представления из xml файла
        val layoutInflater = LayoutInflater.from(context)
        // откуда, куда, необходимо ли помещать в родителя
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(
        messageViewHolder: MessageViewHolder,
        index: Int
    ) {
        messageViewHolder.bind(messageList[index])
    }

    override fun getItemCount(): Int {
        //общее количество элементов в нашем списке
        return messageList.size
    }

    inner class MessageViewHolder(private val view: View) :
        ViewHolder(view) {
        fun bind(message: Message?) {

        }

    }


}