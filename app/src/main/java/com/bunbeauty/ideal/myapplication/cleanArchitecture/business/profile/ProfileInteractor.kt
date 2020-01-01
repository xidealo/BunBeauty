package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IProfileCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.IServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.IServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IUserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class ProfileInteractor(private val userRepository: UserRepository,
                        private val serviceRepository: ServiceRepository,
                        private val intent: Intent) : BaseRepository(),
        IProfileInteractor, IUserCallback, IServiceCallback, IServicesCallback {

    private val TAG = "DBInf"
    private lateinit var profileCallback: IProfileCallback

    override fun updateProfile(ownerId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadProfile(ownerId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCountOfRates(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isFirstEnter() = (intent.hasExtra(User.USER))

    override fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun getOwnerId(): String {
        return if (intent.hasExtra(User.USER)) {
            (intent.getSerializableExtra(User.USER) as User).id
        } else {
            getUserId()
        }
    }

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

    override fun isUserOwner() = (getUserId() == getOwnerId())

    override fun getProfileOwner(profileCallback: IProfileCallback) {
        this.profileCallback = profileCallback

        if (intent.hasExtra(User.USER)) {
            returnUser(intent.getSerializableExtra(User.USER) as User)
        } else {
            userRepository.getById(getOwnerId(),
                    this,
                    isFirstEnter(getOwnerId(), cachedUserIds))
        }
    }

    private fun getProfileServiceList(profileCallback: IProfileCallback) {
        this.profileCallback = profileCallback

        serviceRepository.getServicesByUserId(getOwnerId(),
                this,
                isFirstEnter(getOwnerId(), cachedUserIdsForServices))
    }

    private fun isFirstEnter(id:String, idList: ArrayList<String>):Boolean{
        if(idList.contains(id)){
            return false
        }
        idList.add(id)
        return true
    }

    override fun returnUser(user: User) {
        profileCallback.callbackGetUser(user)
        getProfileServiceList(profileCallback)
    }

    override fun returnServices(serviceList: List<Service>) {
        profileCallback.callbackGetServiceList(serviceList)
    }

    override fun returnService(service: Service) {}

    companion object{
        const val OWNER_ID = "owner id"
        const val TOKEN = "token"
        val cachedUserIds = arrayListOf<String>()
        val cachedUserIdsForServices = arrayListOf<String>()
    }

}