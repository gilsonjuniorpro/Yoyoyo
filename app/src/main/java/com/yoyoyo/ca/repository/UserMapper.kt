package com.yoyoyo.ca.repository

import com.yoyoyo.ca.model.User

object UserMapper {

    fun userToEntity(user: User) =
        UserEntity(
            1,
            user.uuid,
            user.userName,
            user.userPictureUrl,
            user.token,
            if(user.status!!){
                0
            }else{
                1
            }
        )

    fun entityToUser(entity: UserEntity?) =
        entity.run {
            User(
                entity?.uuid,
                entity?.userName,
                entity?.userPictureUrl,
                entity?.token,
                entity?.status!! > 0
            )
        }
}