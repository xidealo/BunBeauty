package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import javax.inject.Inject

class UserDataSource @Inject
constructor(private val userDao: UserDao) : UserRepo {

    override fun findById(id: Int): List<User> {
        return userDao.findAll()
    }

    override fun findAll(): List<User> {
        return userDao.findAll()
    }

    override fun insert(user: User): Long {
        return userDao.insert(user)
    }

    override fun delete(user: User) {
        return userDao.delete(user)
    }
}