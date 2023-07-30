package de.yanos.data.service.auth

import de.yanos.data.model.user.RegisterUserByPassword
import de.yanos.data.model.user.ResetPassword
import de.yanos.data.model.user.User
import de.yanos.data.model.user.UserSignIn
import de.yanos.data.model.user.UserSignInGoogle
import de.yanos.data.util.LoadState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class AuthRepositoryImpl(
    private val local: AuthLocalSource,
    private val remote: AuthRemoteSource,
    private val dispatcher: CoroutineDispatcher
) : AuthRepository {
    override suspend fun register(email: String, pwd: String, lastName: String, firstName: String): LoadState<User> {
        return withContext(dispatcher) {
            val result = remote.register(RegisterUserByPassword(email = email, pwd = pwd, lastName = lastName, firstName = firstName))
            (result as? LoadState.Data)?.data?.let { user ->
                local.saveUser(user)
            }
            result
        }
    }

    override suspend fun signIn(email: String, pwd: String): LoadState<User> {
        return withContext(dispatcher) {
            val result = remote.signIn(UserSignIn(email = email, pwd = pwd))
            (result as? LoadState.Data)?.data?.let { user ->
                local.saveUser(user)
            }
            result
        }
    }

    override suspend fun signInGoogle(id: String, token: String): LoadState<User> {
        return withContext(dispatcher) {
            val result = remote.signInGoogle(UserSignInGoogle(email = id, token = token))
            (result as? LoadState.Data)?.data?.let { user ->
                local.saveUser(user)
            }
            result
        }
    }

    override suspend fun signOut(): LoadState<Boolean> {
        return withContext(dispatcher) { remote.signOut() }
    }

    override suspend fun resetPassword(email: String, newPwd: String): LoadState<User> {
        return withContext(dispatcher) {
            val result = remote.resetPassword(ResetPassword(email = email, pwd = newPwd))
            (result as? LoadState.Data)?.data?.let { user ->
                local.saveUser(user)
            }
            result
        }
    }

    override suspend fun token(id: String): LoadState<String> {
        return withContext(dispatcher) { remote.token(local.loadUser(id)) }
    }
}