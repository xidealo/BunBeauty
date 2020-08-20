package com.bunbeauty.ideal.myapplication.clean_architecture.business

import androidx.recyclerview.widget.DiffUtil
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

class DiffUtilCallback(
    private val oldList: List<Message>,
    private val newList: List<Message>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

}