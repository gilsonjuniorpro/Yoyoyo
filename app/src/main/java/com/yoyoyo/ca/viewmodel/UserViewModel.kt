package com.yoyoyo.ca.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoyoyo.ca.model.User
import com.yoyoyo.ca.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User> = _user

    fun onCreate(user: User) {
        viewModelScope.launch {
            //_user.value = repository.getUser(uuid)
            repository.save(user)
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.save(user)
            }
        }
    }

    fun getUser(uuid: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _user.value = repository.getUser(uuid)
            }
        }
    }

    fun onRecoverUser(uuid: String) {
        viewModelScope.launch {
            _user.value = repository.getUser(uuid)
        }
    }
}