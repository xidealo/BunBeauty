package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val userFirebaseApi: UserFirebaseApi
) : BaseRepository(),
    IUserRepository, IUserCallback, IUsersCallback {

    lateinit var userSubscriber: IUserCallback
    lateinit var usersSubscriber: IUsersCallback

    override fun insert(user: User, iInsertUsersCallback: IInsertUsersCallback) {
        launch {
            userDao.insert(user)
            userFirebaseApi.insert(user)

            withContext(Dispatchers.Main) {
                iInsertUsersCallback.returnCreatedCallback(user)
            }
        }
    }

    override fun delete(user: User, iDeleteUsersCallback: IDeleteUsersCallback) {
        launch {
            userDao.delete(user)
            userFirebaseApi.delete(user)
            withContext(Dispatchers.Main) {
                iDeleteUsersCallback.returnDeletedCallback()
            }
        }
    }

    override fun update(user: User, iUpdateUsersCallback: IUpdateUsersCallback) {
        launch {
            userDao.update(user)
            userFirebaseApi.update(user)
            withContext(Dispatchers.Main) {
                iUpdateUsersCallback.returnUpdatedCallback(user)
            }
        }
    }

    override fun get(iUsersCallback: IUsersCallback) {
        launch {
            val users = userDao.get()
            withContext(Dispatchers.Main) {
                iUsersCallback.returnUsers(users)
            }
        }
    }

    override fun getById(id: String, userSubscriber: IUserCallback, isFirstEnter: Boolean) {
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getById(id, this)
        } else {
            launch {
                val users = userDao.getById(id)
                withContext(Dispatchers.Main) {
                    userSubscriber.returnUser(users)
                }
            }
        }
    }

    override fun getByPhoneNumber(
        phoneNumber: String,
        userSubscriber: IUserCallback,
        isFirstEnter: Boolean
    ) {
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getByPhoneNumber(phoneNumber, this)
        } else {
            launch {
                val users = userDao.getByPhoneNumber(phoneNumber)
                withContext(Dispatchers.Main) {
                    userSubscriber.returnUser(users)
                }
            }
        }
    }

    override fun getByCity(city: String, usersSubscriber: IUsersCallback, isFirstEnter: Boolean) {
        this.usersSubscriber = usersSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getByCity(city, this)
        } else {
            launch {
                val users = userDao.getByCity(city)
                withContext(Dispatchers.Main) {
                    usersSubscriber.returnUsers(users)
                }
            }
        }
    }

    override fun getByCityAndUserName(
        city: String,
        userName: String,
        usersSubscriber: IUsersCallback,
        isFirstEnter: Boolean
    ) {
        this.usersSubscriber = usersSubscriber
        if (isFirstEnter) {
            userFirebaseApi.getByCityAndUserName(city, userName, this)
        } else {
            launch {
                val users = userDao.getByCityAndUserName(city, userName)
                withContext(Dispatchers.Main) {
                    usersSubscriber.returnUsers(users)
                }
            }
        }
    }

    override fun getByName(name: String, usersSubscriber: IUsersCallback, isFirstEnter: Boolean) {
        this.usersSubscriber = usersSubscriber
        if (isFirstEnter) {
            userFirebaseApi.getByName(name, this)
        } else {
            launch {
                val users = userDao.getByName(name)
                withContext(Dispatchers.Main) {
                    usersSubscriber.returnUsers(users)
                }
            }
        }
    }
    //Insert after get in FB
    override fun returnUser(user: User) {
        //убрать проверку?
        if (user.name != "") {
            insertInRoom(user)
        }
        userSubscriber.returnUser(user)
    }

    override fun returnUsers(users: List<User>) {
        for (user in users) {
            insertInRoom(user)
        }
        usersSubscriber.returnUsers(users)
    }

    override fun insertInRoom(user: User) {
        launch {
            userDao.insert(user)
        }
    }
}