package com.bunbeauty.ideal.myapplication.clean_architecture.business

import androidx.recyclerview.widget.DiffUtil
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

class DiffUtilCallback(
    private val oldList: List<MainScreenData>,
    private val newList: List<MainScreenData>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].service.id == newList[newItemPosition].service.id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

}