package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface IUserRepository {
    fun insert(user: User)
    fun delete(user: User)
    fun deleteById(id: String)
    fun update(user: User)
    fun get(): List<User>

    fun getById(id:String, userSubscriber: IUserSubscriber)
    fun getByPhoneNumber(phoneNumber:String, userSubscriber: IUserSubscriber)
    fun returnUser(user: User)
}