package de.yanos.data.service.auth

import android.content.Context
import de.yanos.data.model.user.User
import de.yanos.data.util.LibDI
import de.yanos.data.util.LoadState
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit

interface AuthRepository {

    suspend fun register(email: String, pwd: String, lastName: String, firstName: String): LoadState<User>
    suspend fun signIn(email: String, pwd: String): LoadState<User>
    suspend fun signInGoogle(id: String, token: String): LoadState<User>
    suspend fun signOut(): LoadState<Boolean>
    suspend fun resetPassword(email: String, newPwd: String): LoadState<User>
    suspend fun token(id: String): LoadState<String>

    companion object {

        fun create(context: Context, retrofit: Retrofit, dispatcher: CoroutineDispatcher): AuthRepository {
            return AuthRepositoryImpl(local = LibDI.localAuthSource(context), remote = LibDI.remote, dispatcher = dispatcher)
        }
    }
}