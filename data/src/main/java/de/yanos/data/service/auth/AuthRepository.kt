package de.yanos.data.service.auth

import de.yanos.data.model.user.User
import de.yanos.data.util.LoadState

interface AuthRepository {
    suspend fun loadUserInformation(id: String): User?
    suspend fun register(email: String, pwd: String, lastName: String, firstName: String): LoadState<User>
    suspend fun signIn(email: String, pwd: String): LoadState<User>
    suspend fun signInGoogle(id: String, token: String): LoadState<User>
    suspend fun signOut(id:String): LoadState<Boolean>
    suspend fun resetPassword(email: String, newPwd: String): LoadState<User>
    suspend fun token(id: String): LoadState<String>
}