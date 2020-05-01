package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.IServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IUserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class ProfileUserInteractor(
    private val userRepository: UserRepository,
    private val intent: Intent
) : BaseRepository(),
    IProfileUserInteractor, IUserCallback {

    private val TAG = "DBInf"
    private lateinit var profilePresenterCallback: ProfilePresenterCallback
    private lateinit var currentUser: User

    override fun getCurrentUser(): User = currentUser

    override fun getMyId(): String {
        TODO("Not yet implemented")
    }

    override fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback) {
        this.profilePresenterCallback = profilePresenterCallback
        if (intent.hasExtra(User.USER)) {
            returnUser(intent.getSerializableExtra(User.USER) as User)
            whoseProfile((intent.getSerializableExtra(User.USER) as User), profilePresenterCallback)
        } else {
            userRepository.getById(
                User.getMyId(),
                this,
                isFirstEnter(User.getMyId(), cachedUserIds)
            )
        }
    }

    override fun returnUser(user: User) {
        profilePresenterCallback.setUserProfile(user)
        setRating(user.rating, profilePresenterCallback)
        currentUser = user

        profilePresenterCallback.getProfileServiceList(user.id)
        whoseProfile(user, profilePresenterCallback)
    }

    private fun setRating(rating: Float, profilePresenterCallback: ProfilePresenterCallback) {
        if (rating > 0) {
            profilePresenterCallback.showRating(rating)
        } else {
            profilePresenterCallback.showWithoutRating()
        }
    }

    private fun isFirstEnter() = (intent.hasExtra(User.USER))

    private fun whoseProfile(user: User, profilePresenterCallback: ProfilePresenterCallback) {
        if (user.id == User.getMyId()) {
            profilePresenterCallback.showMyProfile(user)
            cacheCurrentUser = currentUser
        } else {
            profilePresenterCallback.showAlienProfile(user)
        }
    }

    override fun initFCM() {
        if (isFirstEnter()) {
            val token = FirebaseInstanceId.getInstance().token
            val reference = FirebaseDatabase.getInstance().reference
            reference.child(User.USERS)
                .child(User.getMyId())
                .child(TOKEN)
                .setValue(token)
        }
    }

    override fun checkIconClick(profilePresenterCallback: ProfilePresenterCallback) {
        if (currentUser.id == User.getMyId()) {
            profilePresenterCallback.goToEditProfile(currentUser)
        } else {
            profilePresenterCallback.subscribe()
        }
    }

    private fun isFirstEnter(id: String, idList: ArrayList<String>): Boolean {
        if (idList.contains(id)) {
            return false
        }
        idList.add(id)
        return true
    }

    override fun getCountOfRates(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkSubscription(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val OWNER_ID = "owner id"
        const val TOKEN = "token"
        val cachedUserIds = arrayListOf<String>()
        val cachedUserIdsForServices = arrayListOf<String>()
        var cacheCurrentUser = User()
    }
}