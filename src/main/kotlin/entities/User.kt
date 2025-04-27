package com.entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object Users : UUIDTable("users") {
    val username = varchar("username", 12)
    val email = varchar("email", 100).uniqueIndex()
    val password = varchar("password", 64)
    val profileImage = varchar("profile_image", 64).nullable()
}

class UserEntity(id : EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(Users)
    var username by Users.username
    var email by Users.email
    var password by Users.password
    var profileImage by Users.profileImage
}