package com.utils

import org.mindrot.jbcrypt.BCrypt

class PasswordUtils {
    private val BCRYPT_ROUNDS = 12

    fun hash(password : String) : String {
        require(password.isNotEmpty()) {
            "Password Cannot be empty"
        }
        return BCrypt.hashpw(password , BCrypt.gensalt(BCRYPT_ROUNDS))
    }

    fun verify(password : String , hash : String) : Boolean {
        require(password.isNotEmpty() && hash.isNotEmpty()){
            "password and hash cannot be empty"
        }
        println("password :" + BCrypt.checkpw(password , hash))
        return BCrypt.checkpw(password , hash)
    }
}