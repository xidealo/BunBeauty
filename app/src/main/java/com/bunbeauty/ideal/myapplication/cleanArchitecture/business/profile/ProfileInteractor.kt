package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IServiceSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ProfileCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class ProfileInteractor(private val userRepository: UserRepository,
                        private val serviceRepository: ServiceRepository,
                        private val intent: Intent) : BaseRepository(),
        IProfileInteractor, IUserSubscriber, IServiceSubscriber {
    private val TAG = "DBInf"

    lateinit var profileCallback: ProfileCallback

    override fun updateProfile(ownerId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadProfile(ownerId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCountOfRates(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun returnService(service: Service) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isFirstEnter() = (intent.getStringExtra(OWNER_ID) == null)

    override fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun getOwnerId() = intent.getStringExtra(OWNER_ID) ?: getUserId()

    override fun checkSubscription(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initFCM() {
        val token = FirebaseInstanceId.getInstance().token
        val reference = FirebaseDatabase.getInstance().reference
        reference.child(User.USERS)
                .child(getUserId())
                .child(TOKEN)
                .setValue(token)
    }

    override fun isUserOwner():Boolean {
        return getUserId() == getOwnerId()
    }

    override fun getProfileOwner(profileCallback: ProfileCallback) {
        this.profileCallback = profileCallback

        userRepository.getById(getOwnerId(), this, isFirstEnter(getOwnerId()))
    }

    override fun getProfileServiceList(profileCallback: ProfileCallback) {
        this.profileCallback = profileCallback

        serviceRepository.getServicesByUserId(getOwnerId(), this)
    }

    private fun isFirstEnter(id:String):Boolean{
        if(idS.contains(id)){
            return false
        }
        idS.add(id)
        return true
    }

    override fun returnUserAdded(user: User) {
        profileCallback.callbackGetUser(user)
    }

    override fun returnServiceList(serviceList: List<Service>) {
        profileCallback.callbackGetServiceList(serviceList)
    }

    companion object{
        const val OWNER_ID = "owner id"
        const val TOKEN = "token"
        val idS = arrayListOf<String>()
    }
}