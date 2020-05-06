package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository

class ProfileUserInteractor(
    private val userRepository: IUserRepository,
    private val intent: Intent
) : BaseRepository(),
    IProfileUserInteractor, UsersCallback {

    private val TAG = "DBInf"
    private lateinit var profilePresenterCallback: ProfilePresenterCallback
    private lateinit var currentUser: User

    override fun getCurrentUser(): User = currentUser

    override fun updateUser(user: User,  profilePresenterCallback: ProfilePresenterCallback) {
        currentUser = user
        cacheCurrentUser = user
        profilePresenterCallback.showMyProfile(user)
    }

    override fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback) {
        this.profilePresenterCallback = profilePresenterCallback
        if (intent.hasExtra(User.USER)) {
            returnUsers(listOf(intent.getSerializableExtra(User.USER) as User))
            //whoseProfile((intent.getSerializableExtra(User.USER) as User), profilePresenterCallback)
        } else {
            userRepository.getById(
                User.getMyId(),
                this,
                true
            )
        }
    }

    override fun returnUsers(users: List<User>) {
        if(users.isNotEmpty()) {
            val user = users.first()
            profilePresenterCallback.setUserProfile(user)
            setRating(user.rating, profilePresenterCallback)
            currentUser = user
            profilePresenterCallback.getProfileServiceList(user.id)
            whoseProfile(user, profilePresenterCallback)
        }
    }

    private fun setRating(rating: Float, profilePresenterCallback: ProfilePresenterCallback) {
        if (rating > 0) {
            profilePresenterCallback.showRating(rating)
        } else {
            profilePresenterCallback.showWithoutRating()
        }
    }

    //private fun fromRegistration() = (intent.hasExtra())

    private fun whoseProfile(user: User, profilePresenterCallback: ProfilePresenterCallback) {
        if (user.id == User.getMyId()) {
            profilePresenterCallback.showMyProfile(user)
            cacheCurrentUser = currentUser
        } else {
            profilePresenterCallback.showAlienProfile(user)
        }
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
        if (currentUser.id == User.getMyId()) {
            profilePresenterCallback.goToEditProfile(currentUser)
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