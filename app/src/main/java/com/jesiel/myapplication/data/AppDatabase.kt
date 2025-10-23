package com.jesiel.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jesiel.myapplication.model.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}