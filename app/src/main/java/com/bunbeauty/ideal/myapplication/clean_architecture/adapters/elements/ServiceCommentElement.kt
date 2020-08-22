package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.CurrentCommentActivity
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso

class ServiceCommentElement(
    private val context: Context
) {
    private lateinit var avatarServiceCommentElementImage: ImageView
    private lateinit var nameServiceCommentElementText: TextView
    private lateinit var reviewServiceCommentElementText: TextView
    private lateinit var ratingServiceCommentElementBar: RatingBar
    private lateinit var commentElementMaterialCardView: MaterialCardView
    private lateinit var serviceComment: ServiceComment

    fun createElement(view: View) {
        avatarServiceCommentElementImage = view.findViewById(R.id.element_servicecomment_iv_avatar)
        nameServiceCommentElementText = view.findViewById(R.id.element_servicecomment_tv_name)
        reviewServiceCommentElementText = view.findViewById(R.id.element_servicecomment_tv_review)
        ratingServiceCommentElementBar = view.findViewById(R.id.element_servicecomment_rb_rating)
        commentElementMaterialCardView = view.findViewById(R.id.element_servicecomment_ll_main)
        commentElementMaterialCardView.setOnClickListener {
            goToCurrentComment()
        }
    }

    fun setData(serviceComment: ServiceComment) {
        this.serviceComment = serviceComment

        Picasso.get()
            .load(serviceComment.user.photoLink)
            .resize(
                context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width),
                context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width)
            )
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