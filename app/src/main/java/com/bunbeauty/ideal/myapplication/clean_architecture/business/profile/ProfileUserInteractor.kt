package com.bunbeauty.ideal.myapplication.clean_architecture.business.profile

import android.content.Intent
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.iProfile.IProfileUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UpdateUsersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IUserRepository
import com.google.firebase.iid.FirebaseInstanceId

class ProfileUserInteractor(
    private val userRepository: IUserRepository,
    private val intent: Intent
) : BaseRepository(), IProfileUserInteractor, UserCallback, UpdateUsersCallback {

    override lateinit var owner: User

    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    override fun initFCM() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            userRepository.setToken(instanceIdResult.token)
        }
    }

    override fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback) {
        this.profilePresenterCallback = profilePresenterCallback

        if (intent.hasExtra(User.USER)) {
            val user = intent.getSerializableExtra(User.USER) as User
            returnGottenObject(user)
        } else {
            userRepository.getById(
                intent.getStringExtra(User.USER_ID) ?: User.getMyId(),
                this,
                true
            )
        }
    }

    override fun returnGottenObject(user: User?) {
        if (user == null) return

        owner = user
        profilePresenterCallback.returnProfileOwner(user)
        profilePresenterCallback.showCountOfSubscriber(user.subscribersCount)
        whoseProfile(user, profilePresenterCallback)
    }

    override fun whoseProfile(user: User, profilePresenterCallback: ProfilePresenterCallback) {
        if (isMyProfile(user.id, User.getMyId())) {
            cacheUser = user
            profilePresenterCallback.showMyProfile(user)
            profilePresenterCallback.showUpdatedBottomPanel(R.id.navigation_profile)
        } else {
            profilePresenterCallback.showAlienProfile(user)
            profilePresenterCallback.showUpdatedBottomPanel()
        }
    }

    override fun isMyProfile(ownerId: String, myId: String) = ownerId == myId

   override fun checkProfileToUpdateOrders(profilePresenterCallback: ProfilePresenterCallback) {
        if (isMyProfile(owner.id, User.getMyId())) {
            profilePresenterCallback.getOrderList(owner.id)
        }
    }

    override fun updateUserFromEditUser(
        user: User,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        returnGottenObject(user)
    }

    override fun updateCountOfSubscribers(
        user: User,
        subscriber: Int,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        user.subscribersCount += subscriber
        userRepository.update(user, this)
    }

    override fun returnUpdatedCallback(user: User) {
        owner = user
        profilePresenterCallback.showCountOfSubscriber(user.subscribersCount)
    }

    companion object {
        var cacheUser = User()
    }
}