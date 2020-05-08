package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.DeleteUsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.InsertUsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UpdateUsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UsersCallback
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
) : BaseRepository(), IUserRepository, UsersCallback {

    lateinit var usersCallback: UsersCallback

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

    override fun getById(id: String, usersCallback: UsersCallback, isFirstEnter: Boolean) {
        this.usersCallback = usersCallback

        if (isFirstEnter) {
            userFirebase.getById(id, this)
        } else {
            launch {
                val users = userDao.getById(id)

                withContext(Dispatchers.Main) {
                    usersCallback.returnUsers(users)
                }
            }
        }
    }

    override fun getByPhoneNumber(
        phoneNumber: String,
        usersCallback: UsersCallback,
        isFirstEnter: Boolean
    ) {
        this.usersCallback = usersCallback

        if (isFirstEnter) {
            userFirebase.getByPhoneNumber(phoneNumber, this)
        } else {
            launch {
                val users = userDao.getByPhoneNumber(phoneNumber)
                withContext(Dispatchers.Main) {
                    usersCallback.returnUsers(users)
                }
            }
        }
    }

    override fun getByCity(city: String, usersCallback: UsersCallback, isFirstEnter: Boolean) {
        this.usersCallback = usersCallback

        if (isFirstEnter) {
            userFirebase.getByCity(city, this)
        } else {
            launch {
                val users = userDao.getByCity(city)
                withContext(Dispatchers.Main) {
                    usersCallback.returnUsers(users)
                }
            }
        }
    }

    override fun getByCityAndUserName(
        city: String,
        userName: String,
        usersCallback: UsersCallback,
        isFirstEnter: Boolean
    ) {
        this.usersCallback = usersCallback
        if (isFirstEnter) {
            userFirebase.getByCityAndUserName(city, userName, this)
        } else {
            launch {
                val users = userDao.getByCityAndUserName(city, userName)
                withContext(Dispatchers.Main) {
                    usersCallback.returnUsers(users)
                }
            }
        }
    }

    override fun getByName(name: String, usersCallback: UsersCallback, isFirstEnter: Boolean) {
        this.usersCallback = usersCallback
        if (isFirstEnter) {
            userFirebase.getByName(name, this)
        } else {
            launch {
                val users = userDao.getByName(name)
                withContext(Dispatchers.Main) {
                    usersCallback.returnUsers(users)
                }
            }
        }
    }

    override fun returnUsers(users: List<User>) {
      /*  for (user in users) {
            insertInRoom(user)
        }*/
        usersCallback.returnUsers(users)
    }

    override fun insertInRoom(user: User) {
        launch {
            userDao.insert(user)
        }
    }
}