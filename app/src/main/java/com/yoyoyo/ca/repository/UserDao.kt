package com.yoyoyo.ca.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(user: UserEntity): Long

    @Delete
    suspend fun delete(vararg user: UserEntity): Int

    @Query("SELECT * FROM UserEntity")
    fun getUser(): Flow<List<UserEntity>>

    @Query("SELECT * FROM UserEntity WHERE uuid = :uuid")
    suspend fun getUser(uuid: String) : UserEntity?
}