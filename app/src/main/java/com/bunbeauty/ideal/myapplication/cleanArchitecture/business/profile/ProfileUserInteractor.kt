package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository

class ProfileUserInteractor(
    private val userRepository: IUserRepository,
    private val intent: Intent
) : BaseRepository(), IProfileUserInteractor, UsersCallback {

    private lateinit var profilePresenterCallback: ProfilePresenterCallback
    private var currentUser: User? = null

    override fun getCurrentUser(): User = currentUser!!

    override fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback) {
        this.profilePresenterCallback = profilePresenterCallback
        if (intent.hasExtra(User.USER)) {
            val user = intent.getSerializableExtra(User.USER) as User
            if (user.id.isNotEmpty()) {
                returnUsers(listOf(user))
            } else {
                userRepository.getById(
                    User.getMyId(),
                    this,
                    true
                )
            }
        } else {
            userRepository.getById(
                User.getMyId(),
                this,
                true
            )
        }
    }

    override fun returnUsers(users: List<User>) {
        if (users.isNotEmpty()) {
            val user = users.first()
            currentUser = user
            profilePresenterCallback.returnProfileOwner(user)

            whoseProfile(user, profilePresenterCallback)
        }
    }

    private fun whoseProfile(user: User, profilePresenterCallback: ProfilePresenterCallback) {
        if (isMyProfile(user.id, User.getMyId())) {
            profilePresenterCallback.showMyProfile(user)
            cacheCurrentUser = currentUser!!
        } else {
            profilePresenterCallback.showAlienProfile(user)
        }
    }

    override fun isMyProfile(ownerId: String, myId: String) = ownerId == myId

    override fun updateUser(user: User, profilePresenterCallback: ProfilePresenterCallback) {
        currentUser = user
        cacheCurrentUser = user
        profilePresenterCallback.showMyProfile(user)
    }

    override fun initFCM() {
        // if (fromRegistartion()) {
        /*      val token = FirebaseInstanceId.getInstance().token
              val reference = FirebaseDatabase.getInstance().reference
              reference.child(User.USERS)
                  .child(User.getMyId())
                  .child(TOKEN)
                  .setValue(token)*/
        //}
    }

    override fun checkIconClick(profilePresenterCallback: ProfilePresenterCallback) {
        if (currentUser!!.id == User.getMyId()) {
            profilePresenterCallback.goToEditProfile(currentUser!!)
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

    override fun updateBottomPanel(profilePresenterCallback: ProfilePresenterCallback) {
        if (currentUser == null) {
            return
        }

        if (currentUser!!.id == User.getMyId()) {
            profilePresenterCallback.showUpdatedBottomPanel(R.id.navigation_profile)
        } else {
            profilePresenterCallback.showUpdatedBottomPanel()
        }
    }

    companion object {
        const val OWNER_ID = "owner id"
        const val TOKEN = "token"
        val cachedUserIds = arrayListOf<String>()
        val cachedUserIdsForServices = arrayListOf<String>()
        var cacheCurrentUser = User()
    }
}