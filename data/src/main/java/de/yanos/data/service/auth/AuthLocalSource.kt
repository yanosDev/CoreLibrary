package de.yanos.data.service.auth

import de.yanos.data.model.user.User

interface AuthLocalSource {
    suspend fun saveUser(user: User)
    suspend fun loadUser(id: String): User?

}