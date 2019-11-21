package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IUserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserRepository(private val userDao: UserDao,
                     private val userFirebaseApi: UserFirebaseApi) : BaseRepository(),
        IUserRepository, IUserCallback {

    private val TAG = "data_layer"

    lateinit var userSubscriber: IUserCallback

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

    override fun getById(id: String, userSubscriber: IUserCallback, isFirstEnter: Boolean) {
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getById(id, this)
        } else {
            var user: User? = null
            runBlocking {
                user = userDao.getById(id)
            }
            userSubscriber.returnUser(user!!)
        }
    }

    override fun getByPhoneNumber(phoneNumber: String, userSubscriber: IUserCallback, isFirstEnter: Boolean) {
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getByPhoneNumber(phoneNumber, this)
        } else {
            var user: User? = null
            runBlocking {
                user = userDao.getByPhoneNumber(phoneNumber)
            }
            userSubscriber.returnUser(user!!)
        }
    }

    override fun getByCity(city: String, userSubscriber: IUserCallback, isFirstEnter: Boolean) {
        var users = listOf<User>()
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getByCity(city, this)
        } else {
            runBlocking {
                users = userDao.getByCity(city)
            }
            userSubscriber.returnUsers(users)
        }
    }

    override fun getByCityAndUserName(city: String, userName:String, userSubscriber: IUserCallback, isFirstEnter: Boolean) {
        var users = listOf<User>()
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getByCityAndUserName(city,userName, this)
        } else {
            runBlocking {
                users = userDao.getByCityAndUserName(city, userName)
            }
            userSubscriber.returnUsers(users)
        }
    }

    override fun getByName(name: String, userSubscriber: IUserCallback, isFirstEnter: Boolean) {
        var users = listOf<User>()
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getByName(name, this)
        } else {
            runBlocking {
                users = userDao.getByName(name)
            }
            userSubscriber.returnUsers(users)
        }
    }

    override fun returnUser(user: User) {
        if (user.name != "") {
            launch {
                userDao.insert(user)
            }
        }
        userSubscriber.returnUser(user)
    }

    override fun returnUsers(users: List<User>) {
        launch {
            for(user in users){
                userDao.insert(user)
            }
        }
        userSubscriber.returnUsers(users)
    }

}