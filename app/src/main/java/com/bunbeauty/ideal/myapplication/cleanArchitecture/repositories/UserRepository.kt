package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IUserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserRepository(private val userDao: UserDao,
                     private val userFirebaseApi: UserFirebaseApi) : BaseRepository(),
        IUserRepository, IUserSubscriber {


    lateinit var userSubscriber: IUserSubscriber

    override fun insert(user: User) {
        launch {
            userDao.insert(user)
        }
        userFirebaseApi.insert(user)
    }

    override fun delete(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(user: User) {
        launch {
            userDao.update(user)
        }
    }

    override fun get(): List<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteById(id: String) {
        launch {
            userDao.deleteById(id)
        }
    }

    private val TAG = "data_layer"

    override fun getById(id: String, userSubscriber: IUserSubscriber, isFirstEnter: Boolean) {
        var user: User? = null
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getById(id, this)
        } else {
            runBlocking {
                user = userDao.getById(id)
            }
            userSubscriber.returnAddedUser(user!!)
        }
    }

    override fun getByPhoneNumber(phoneNumber: String, userSubscriber: IUserSubscriber) {
        this.userSubscriber = userSubscriber

        var user: User? = null
        runBlocking {
            user = userDao.getByPhoneNumber(phoneNumber)
        }

        if (user == null) {
            userFirebaseApi.getByPhoneNumber(phoneNumber, this)
        } else {
            userSubscriber.returnAddedUser(user!!)
        }
    }

    override fun returnAddedUser(user: User) {
        userSubscriber.returnAddedUser(user)

        if (user.name.isNotEmpty()) {
            launch {
                userDao.insert(user)
            }
        }
    }


}