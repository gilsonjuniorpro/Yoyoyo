package com.yoyoyo.ca.repository

import android.content.Context
import com.yoyoyo.ca.model.User

class ChatRepository(context: Context) {

    private val dao: UserDao = AppDatabase.getDatabase(context).getUserDao()

    suspend fun save(user: User) {
        dao.save(UserMapper.userToEntity(user))
    }

    suspend fun delete(user: User) {
        dao.delete(UserMapper.userToEntity(user))
    }

    suspend fun getUser(uuid: String): User? {
        var user = dao.getUser(uuid)
        return if(user != null){
            UserMapper.entityToUser(dao.getUser(uuid))
        }else{
            null
        }
    }

    /*fun allFavorites(): Flow<List<Story>> {
        return dao.allFavorites()
            .map { storyList ->
                storyList.map { storyEntity ->
                    UserMapper.entityToStory(storyEntity)
                }
            }
    }*/

}