package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.currentComment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.currentComment.iCurrentComment.ICurrentCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CurrentCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

class CurrentCommentCommentInteractor(
    private val intent: Intent
) : ICurrentCommentCommentInteractor {

    override fun getComment(currentCommentPresenterCallback: CurrentCommentPresenterCallback) {
        if (intent.hasExtra(UserComment.USER_COMMENT)) {
            currentCommentPresenterCallback.setUserComment(
                intent.getSerializableExtra(
                    UserComment.USER_COMMENT
                ) as UserComment
            )
        } else {
            currentCommentPresenterCallback.setServiceComment(
                intent.getSerializableExtra(
                    ServiceComment.SERVICE_COMMENT
                ) as ServiceComment
            )
        }
    }
}