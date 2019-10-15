package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    suspend fun findAll() : List<User>

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun findById(id: String): User?

    @Query("SELECT * FROM user WHERE phone = :phone")
    suspend fun findByPhoneNumber(phone: String?): User?

    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}