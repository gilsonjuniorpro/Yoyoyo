package com.yoyoyo.ca.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity (
    @PrimaryKey
    val id: Int,
    val uuid: String?,
    val userName: String?,
    val userPictureUrl: String?,
    val token: String?,
    val status: Int?
)
