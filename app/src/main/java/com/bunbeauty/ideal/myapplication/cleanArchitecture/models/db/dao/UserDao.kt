package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao

import android.arch.persistence.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.entity.User

@Dao
public interface UserDao {

    fun getAll() : List<User>

    @Query("SELECT * FROM user WHERE id = :id")
    fun getById(id: Long) : List<User>

    @Insert
    fun insert(employee: User)

    @Update
    fun update(employee: User)

    @Delete
    fun delete(employee: User)
}