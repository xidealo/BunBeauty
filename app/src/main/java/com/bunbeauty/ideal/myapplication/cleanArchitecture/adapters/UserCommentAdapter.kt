
package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.UserCommentElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

class UserCommentAdapter(private var commentList: List<UserComment>) :
    RecyclerView.Adapter<UserCommentAdapter.UserCommentViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UserCommentViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_user_comment
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return this.UserCommentViewHolder(view, context)
    }

    override fun onBindViewHolder(commentViewHolder: UserCommentViewHolder, i: Int) {
        commentViewHolder.bind(commentList[i])
    }

    override fun getItemCount() = commentList.size

    inner class UserCommentViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(userComment: UserComment) {
            val commentElement = UserCommentElement(context)
            commentElement.createElement(view)
            commentElement.setData(userComment)
        }
    }

}
