package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.CurrentCommentActivity
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso

class UserCommentElement(
    private val context: Context
) {
    private lateinit var avatarUserCommentElementImage: ImageView
    private lateinit var nameUserCommentElementText: TextView
    private lateinit var reviewUserCommentElementText: TextView
    private lateinit var ratingUserCommentElementBar: RatingBar
    private lateinit var commentElementMaterialCard: MaterialCardView
    private lateinit var userComment: UserComment

    fun createElement(view: View) {
        avatarUserCommentElementImage = view.findViewById(R.id.element_comment_iv_avatar)
        nameUserCommentElementText = view.findViewById(R.id.element_comment_tv_name)
        reviewUserCommentElementText = view.findViewById(R.id.element_comment_tv_review)
        ratingUserCommentElementBar = view.findViewById(R.id.element_comment_rb_rating)
        commentElementMaterialCard = view.findViewById(R.id.element_comment_ll_main)
        commentElementMaterialCard.setOnClickListener {
            goToCurrentComment()
        }
    }

    fun setData(userComment: UserComment) {
        this.userComment = userComment

        Picasso.get()
            .load(userComment.user.photoLink)
            .resize(
                context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width),
                context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width)
            )
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarUserCommentElementImage)

        nameUserCommentElementText.text = "${userComment.user.name}  ${userComment.user.surname}"
        reviewUserCommentElementText.text = userComment.review
        ratingUserCommentElementBar.rating = userComment.rating
    }

    private fun goToCurrentComment() {
        val intent = Intent(context, CurrentCommentActivity::class.java).apply {
            putExtra(UserComment.USER_COMMENT, userComment)
        }
        context.startActivity(intent)
    }

}