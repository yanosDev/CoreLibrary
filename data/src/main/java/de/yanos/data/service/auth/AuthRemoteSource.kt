package de.yanos.data.service.auth

import de.yanos.data.model.user.RegisterUserByPassword
import de.yanos.data.model.user.ResetPassword
import de.yanos.data.model.user.User
import de.yanos.data.model.user.UserSignIn
import de.yanos.data.model.user.UserSignInGoogle
import de.yanos.data.util.LoadState
import retrofit2.http.Body

internal interface AuthRemoteSource {
    suspend fun register(user: RegisterUserByPassword): LoadState<User>
    suspend fun signIn(user: UserSignIn): LoadState<User>
    suspend fun signInGoogle(@Body user: UserSignInGoogle): LoadState<User>
    suspend fun signOut(): LoadState<Boolean>
    suspend fun resetPassword(user: ResetPassword): LoadState<User>
    suspend fun token(user: User): LoadState<String>
}