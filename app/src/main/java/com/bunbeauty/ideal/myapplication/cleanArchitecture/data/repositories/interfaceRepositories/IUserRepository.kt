package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IUserRepository {
    fun insert(user: User, insertUsersCallback: InsertUsersCallback)
    fun insertInRoom(user: User)
    fun delete(user: User, deleteUsersCallback: DeleteUsersCallback)
    fun update(user: User, updateUsersCallback: UpdateUsersCallback)
    fun get(usersCallback: UsersCallback)

    fun getById(id: String, usersCallback: UsersCallback, isFirstEnter: Boolean)
    fun getByPhoneNumber(phoneNumber: String, usersCallback: UsersCallback, isFirstEnter: Boolean)
    fun getByCity(city: String, usersCallback: UsersCallback, isFirstEnter: Boolean)
    fun getByName(name: String, usersCallback: UsersCallback, isFirstEnter: Boolean)
    fun getByCityAndUserName(
        city: String,
        userName: String,
        usersCallback: UsersCallback,
        isFirstEnter: Boolean
    )
}