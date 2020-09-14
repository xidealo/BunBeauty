package com.bunbeauty.ideal.myapplication.clean_architecture.domain

import androidx.recyclerview.widget.DiffUtil
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog

class DiffUtilCallback(
    private val oldList: List<Dialog>,
    private val newList: List<Dialog>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].user.id == newList[newItemPosition].user.id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

}