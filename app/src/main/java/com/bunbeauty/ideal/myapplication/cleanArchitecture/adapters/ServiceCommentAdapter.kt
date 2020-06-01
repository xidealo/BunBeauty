package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment

class ServiceCommentAdapter(private var serviceCommentList: List<ServiceComment>) :
    RecyclerView.Adapter<ServiceCommentAdapter.ServiceCommentViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServiceCommentViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_service_comment
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return this.ServiceCommentViewHolder(view, context)
    }

    override fun onBindViewHolder(commentViewHolder: ServiceCommentViewHolder, i: Int) {
        commentViewHolder.bind(serviceCommentList[i])
    }

    override fun getItemCount() = serviceCommentList.size

    inner class ServiceCommentViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(serviceComment: ServiceComment) {

        }
    }

}
