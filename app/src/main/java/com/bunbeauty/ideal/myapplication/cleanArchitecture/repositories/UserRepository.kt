package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IUserRepository
import kotlinx.coroutines.launch

class UserRepository(private val userDao: UserDao,
                     private val userFirebaseApi: UserFirebaseApi) : BaseRepository(),
        IUserRepository, IUserCallback, IUsersCallback {

    lateinit var userSubscriber: IUserCallback
    lateinit var usersSubscriber: IUsersCallback
    //TODO(Переделать логику insert, update, id получаем только после инсерта в FB

    override fun insert(user: User, iInsertUsersCallback: IInsertUsersCallback) {
        launch {
            userDao.insert(user)
            userFirebaseApi.insert(user)
            iInsertUsersCallback.returnCreatedCallback(user)
        }
    }

    override fun delete(user: User, iDeleteUsersCallback: IDeleteUsersCallback) {
        launch {
            userDao.delete(user)
            userFirebaseApi.delete(user)
            iDeleteUsersCallback.returnDeletedCallback()
        }
    }

    override fun update(user: User, iUpdateUsersCallback: IUpdateUsersCallback) {
        launch {
            userDao.update(user)
            userFirebaseApi.update(user)
            iUpdateUsersCallback.returnUpdatedCallback(user)
        }
    }

    override fun get(iUsersCallback: IUsersCallback) {
        launch {
            iUsersCallback.returnUsers(userDao.get())
        }
    }

    override fun getById(id: String, userSubscriber: IUserCallback, isFirstEnter: Boolean) {
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getById(id, this)
        } else {
            launch {
                userSubscriber.returnUser(userDao.getById(id))
            }
        }
    }

    override fun getByPhoneNumber(phoneNumber: String, userSubscriber: IUserCallback, isFirstEnter: Boolean) {
        this.userSubscriber = userSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getByPhoneNumber(phoneNumber, this)
        } else {
            launch {
                userSubscriber.returnUser(userDao.getByPhoneNumber(phoneNumber))
            }
        }
    }

    override fun getByCity(city: String, usersSubscriber: IUsersCallback, isFirstEnter: Boolean) {
        this.usersSubscriber = usersSubscriber

        if (isFirstEnter) {
            userFirebaseApi.getByCity(city, this)
        } else {
            launch {
                usersSubscriber.returnUsers(userDao.getByCity(city))
            }
        }
    }

    override fun getByCityAndUserName(city: String, userName: String, usersSubscriber: IUsersCallback, isFirstEnter: Boolean) {
        this.usersSubscriber = usersSubscriber
        if (isFirstEnter) {
            userFirebaseApi.getByCityAndUserName(city, userName, this)
        } else {
            launch {
                usersSubscriber.returnUsers(userDao.getByCityAndUserName(city, userName))
            }
        }
    }

    override fun getByName(name: String, usersSubscriber: IUsersCallback, isFirstEnter: Boolean) {
        this.usersSubscriber = usersSubscriber
        if (isFirstEnter) {
            userFirebaseApi.getByName(name, this)
        } else {
            launch {
                usersSubscriber.returnUsers(userDao.getByName(name))
            }
        }
    }
    //Что за даун это написал?
    //Понял, что это я
    /*
    * Return также интсертит значения в локальную базу данных, после того, как оин были получены из FB
    * Если мы будем инсертить из презентора, то получится, что он заинсертит еще и в FB
     */
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