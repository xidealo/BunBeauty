package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.CurrentCommentActivity
import com.squareup.picasso.Picasso

class ServiceCommentElement(
    private val context: Context
) {
    private lateinit var avatarServiceCommentElementImage: ImageView
    private lateinit var nameServiceCommentElementText: TextView
    private lateinit var reviewServiceCommentElementText: TextView
    private lateinit var ratingServiceCommentElementBar: RatingBar
    private lateinit var commentElementLayout: LinearLayout
    private lateinit var serviceComment: ServiceComment

    fun createElement(view: View) {
        avatarServiceCommentElementImage = view.findViewById(R.id.avatarServiceCommentElementImage)
        nameServiceCommentElementText = view.findViewById(R.id.nameServiceCommentElementText)
        reviewServiceCommentElementText = view.findViewById(R.id.reviewServiceCommentElementText)
        ratingServiceCommentElementBar = view.findViewById(R.id.ratingServiceCommentElementBar)
        commentElementLayout = view.findViewById(R.id.serviceCommentElementLayout)
        commentElementLayout.setOnClickListener {
            goToCurrentComment()
        }
    }

    fun setData(serviceComment: ServiceComment) {
        this.serviceComment = serviceComment

        Picasso.get()
            .load(serviceComment.user.photoLink)
            .resize(65, 65)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarServiceCommentElementImage)

        nameServiceCommentElementText.text =
            "${serviceComment.user.name}  ${serviceComment.user.surname}"
        reviewServiceCommentElementText.text = serviceComment.review
        ratingServiceCommentElementBar.rating = serviceComment.rating
    }

    private fun goToCurrentComment() {
        val intent = Intent(context, CurrentCommentActivity::class.java).apply {
            putExtra(ServiceComment.SERVICE_COMMENT, serviceComment)
        }
        context.startActivity(intent)
    }

}