package com.jesiel.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.jesiel.myapplication.data.DatabaseProvider
import com.jesiel.myapplication.model.User

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    private val userDao = db.userDao()

    fun insertUser(name: String) {
        viewModelScope.launch {
            userDao.insert(User(name = name))
        }
    }

    fun getUsers(): List<User> {
        return userDao.getAll()
    }
}