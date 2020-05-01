package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val userFirebase: UserFirebase
) : BaseRepository(),
    IUserRepository, IUserCallback, UsersCallback {

    lateinit var userSubscriber: IUserCallback
    lateinit var usersSubscriber: UsersCallback

    override fun insert(user: User, insertUsersCallback: InsertUsersCallback) {
        launch {
            userDao.insert(user)
            userFirebase.insert(user)

            withContext(Dispatchers.Main) {
                insertUsersCallback.returnCreatedCallback(user)
            }
        }
    }

    override fun delete(user: User, deleteUsersCallback: DeleteUsersCallback) {
        launch {
            userDao.delete(user)
            userFirebase.delete(user)
            withContext(Dispatchers.Main) {
                deleteUsersCallback.returnDeletedCallback(user)
            }
        }
    }

    override fun update(user: User, updateUsersCallback: UpdateUsersCallback) {
        launch {
            userDao.update(user)
            userFirebase.update(user)
            withContext(Dispatchers.Main) {
                updateUsersCallback.returnUpdatedCallback(user)
            }
        }
    }

    override fun get(usersCallback: UsersCallback) {
        launch {
            val users = userDao.get()
            withContext(Dispatchers.Main) {
                usersCallback.returnUsers(users)
            }
        }
    }

    override fun getById(id: String, userSubscriber: IUserCallback, isFirstEnter: Boolean) {
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebase.getById(id, this)
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
            userFirebase.getByPhoneNumber(phoneNumber, this)
        } else {
            launch {
                val users = userDao.getByPhoneNumber(phoneNumber)
                withContext(Dispatchers.Main) {
                    userSubscriber.returnUser(users)
                }
            }
        }
    }

    override fun getByCity(city: String, usersSubscriber: UsersCallback, isFirstEnter: Boolean) {
        this.usersSubscriber = usersSubscriber

        if (isFirstEnter) {
            userFirebase.getByCity(city, this)
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
        usersSubscriber: UsersCallback,
        isFirstEnter: Boolean
    ) {
        this.usersSubscriber = usersSubscriber
        if (isFirstEnter) {
            userFirebase.getByCityAndUserName(city, userName, this)
        } else {
            launch {
                val users = userDao.getByCityAndUserName(city, userName)
                withContext(Dispatchers.Main) {
                    usersSubscriber.returnUsers(users)
                }
            }
        }
    }

    override fun getByName(name: String, usersSubscriber: UsersCallback, isFirstEnter: Boolean) {
        this.usersSubscriber = usersSubscriber
        if (isFirstEnter) {
            userFirebase.getByName(name, this)
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