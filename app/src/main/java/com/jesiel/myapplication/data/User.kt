package com.jesiel.myapplication.data

import androidx.room.*
import com.jesiel.myapplication.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Insert
    fun insert(user: User)
}