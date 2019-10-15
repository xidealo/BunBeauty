package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IUserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserRepository(val userDao: UserDao, val userFirebaseApi: UserFirebaseApi) : BaseRepository(), IUserRepository {

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(): List<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getById(id: String): User  {
        var user:User? = null
        runBlocking {
            user = userDao.findById(id)
        }

        if (user == null){
            user = userFirebaseApi.getById(id)
            launch {
                userDao.insert(user!!)
            }
        }
        return user!!
    }


}