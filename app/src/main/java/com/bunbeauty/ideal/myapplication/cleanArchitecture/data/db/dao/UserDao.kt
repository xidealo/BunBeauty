package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

@Dao
interface UserDao: BaseDao<User> {

    @Query("SELECT * FROM user")
    suspend fun get() : List<User>

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getById(id: String): List<User>

    @Query("SELECT * FROM user WHERE phone = :phone")
    suspend fun getByPhoneNumber(phone: String): List<User>

    @Query("SELECT * FROM user WHERE city = :city")
    suspend fun getByCity(city: String): List<User>

    @Query("SELECT * FROM user WHERE city = :city AND name = :name")
    suspend fun getByCityAndUserName(city: String, name:String): List<User>

    @Query("SELECT * FROM user WHERE name = :name")
    suspend fun getByName(name: String): List<User>

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Query("DELETE FROM user WHERE id = :id")
    suspend fun deleteById(id: String)
}