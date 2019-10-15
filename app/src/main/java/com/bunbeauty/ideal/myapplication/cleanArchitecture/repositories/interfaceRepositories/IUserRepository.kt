package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface IUserRepository {
    fun insert(user: User)
    fun delete(user: User)
    fun update(user: User)
    fun get(): List<User>

    fun getById(id:String): User
}