package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IUserRepository {
    fun insert(user: User, insertUsersCallback: InsertUsersCallback)
    fun insertInRoom(user: User)
    fun delete(user: User, deleteUsersCallback: DeleteUsersCallback)
    fun update(user: User, updateUsersCallback: UpdateUsersCallback)
    fun get(usersCallback: UsersCallback)
    fun setToken(token: String)
    fun getById(id: String, userCallback: UserCallback, isFirstEnter: Boolean)
    fun getByPhoneNumber(phoneNumber: String, userCallback: UserCallback, isFirstEnter: Boolean)
    fun getByCity(city: String, usersCallback: UsersCallback, isFirstEnter: Boolean)
    fun getByName(name: String, usersCallback: UsersCallback, isFirstEnter: Boolean)
    fun getByCityAndUserName(
        city: String,
        userName: String,
        usersCallback: UsersCallback,
        isFirstEnter: Boolean
    )
}