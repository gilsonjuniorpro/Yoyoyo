package com.yoyoyo.ca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yoyoyo.ca.repository.ChatRepository
import java.lang.IllegalArgumentException

class ChatViewModelFactory(val repository: ChatRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserViewModel::class.java)){
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unkown ViewModel Class")
    }
}