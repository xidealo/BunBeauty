package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.UserFirebase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRepository(
    private val userFirebase: UserFirebase
) : BaseRepository(), IUserRepository {

    override fun insert(user: User, insertUsersCallback: InsertUsersCallback) {
        launch {
            //userDao.insert(user)
            userFirebase.insert(user)

            withContext(Dispatchers.Main) {
                insertUsersCallback.returnCreatedCallback(user)
            }
        }
    }

    override fun delete(user: User, deleteUsersCallback: DeleteUsersCallback) {
        launch {
            //userDao.delete(user)
            userFirebase.delete(user)
            withContext(Dispatchers.Main) {
                deleteUsersCallback.returnDeletedCallback(user)
            }
        }
    }

    override fun update(user: User, updateUsersCallback: UpdateUsersCallback) {
        launch {
            //userDao.update(user)
            userFirebase.update(user)
            withContext(Dispatchers.Main) {
                updateUsersCallback.returnUpdatedCallback(user)
            }
        }
    }

    override fun get(usersCallback: UsersCallback) {
        launch {
            //val users = userDao.get()
            withContext(Dispatchers.Main) {
                //usersCallback.returnList(users)
            }
        }
    }

    override fun setToken(token: String) {
        userFirebase.setToken(token)
    }

    override fun getById(id: String, userCallback: UserCallback, isFirstEnter: Boolean) {
        if (isFirstEnter) {
            userFirebase.getById(id, userCallback)
        } else {
            launch {
                //val users = userDao.getById(id)

                withContext(Dispatchers.Main) {
                    //userCallback.returnElement(users)
                }
            }
        }
    }

    override fun getByPhoneNumber(
        phoneNumber: String,
        userCallback: UserCallback,
        isFirstEnter: Boolean
    ) {
        if (isFirstEnter) {
            userFirebase.getByPhoneNumber(phoneNumber, userCallback)
        } else {
            launch {
                //val users = userDao.getByPhoneNumber(phoneNumber)
                withContext(Dispatchers.Main) {
                    //isFirstEnter.returnList(users)
                }
            }
        }
    }

    override fun getByCity(city: String, usersCallback: UsersCallback, isFirstEnter: Boolean) {
        if (isFirstEnter) {
            userFirebase.getByCity(city, usersCallback)
        } else {
            launch {
                //val users = userDao.getByCity(city)
                withContext(Dispatchers.Main) {
                   // usersCallback.returnList(users)
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
        if (isFirstEnter) {
            userFirebase.getByCityAndUserName(city, userName, usersCallback)
        } else {
            launch {
                //val users = userDao.getByCityAndUserName(city, userName)
                withContext(Dispatchers.Main) {
                   // usersCallback.returnList(users)
                }
            }
        }
    }

    override fun getByName(name: String, usersCallback: UsersCallback, isFirstEnter: Boolean) {
        if (isFirstEnter) {
            userFirebase.getByName(name, usersCallback)
        } else {
            launch {
                //val users = userDao.getByName(name)
                withContext(Dispatchers.Main) {
                   // usersCallback.returnList(users)
                }
            }
        }
    }

    override fun insertInRoom(user: User) {
        launch {
            //userDao.insert(user)
        }
    }
}