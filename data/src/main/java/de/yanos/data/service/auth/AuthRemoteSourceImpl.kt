package de.yanos.data.service.auth

import de.yanos.core.utils.IODispatcher
import de.yanos.data.api.AuthApi
import de.yanos.data.model.user.RegisterUserByPassword
import de.yanos.data.model.user.ResetPassword
import de.yanos.data.model.user.User
import de.yanos.data.model.user.UserSignIn
import de.yanos.data.model.user.UserSignInGoogle
import de.yanos.data.util.LoadState
import de.yanos.data.util.toQueryMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

internal class AuthRemoteSourceImpl @Inject constructor(
    private val api: AuthApi,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : AuthRemoteSource {
    override suspend fun register(user: RegisterUserByPassword): LoadState<User> {
        return withContext(dispatcher) {
            catchException {
                api.register(user).awaitResponse().let { response ->
                    response.takeIf { it.isSuccessful }?.body()?.let { user ->
                        LoadState.Data(data = user)
                    } ?: LoadState.Failure(Exception("${response.code()} - ${response.message()}"))
                }
            }
        }
    }


    override suspend fun signIn(user: UserSignIn): LoadState<User> {
        return withContext(dispatcher) {
            catchException {
                api.signIn(user).awaitResponse().let { response ->
                    response.takeIf { it.isSuccessful }?.body()?.let { user ->
                        LoadState.Data(data = user)
                    } ?: LoadState.Failure(Exception(response.errorBody().toString()))
                }
            }
        }
    }

    override suspend fun signInGoogle(user: UserSignInGoogle): LoadState<User> {
        return withContext(dispatcher) {
            catchException {
                api.signInGoogle(user).awaitResponse().let { response ->
                    response.takeIf { it.isSuccessful }?.body()?.let { user ->
                        LoadState.Data(data = user)
                    } ?: LoadState.Failure(Exception(response.errorBody().toString()))
                }
            }
        }
    }

    override suspend fun signOut(): LoadState<Boolean> {
        return withContext(dispatcher) {
            catchException {
                api.signOut().awaitResponse().let { response ->
                    response.takeIf { it.isSuccessful }?.body()?.let { hasSucceeded ->
                        LoadState.Data(data = hasSucceeded)
                    } ?: LoadState.Failure(Exception(response.errorBody().toString()))
                }
            }
        }
    }

    override suspend fun resetPassword(user: ResetPassword): LoadState<User> {
        return withContext(dispatcher) {
            catchException {
                api.resetPassword(user).awaitResponse().let { response ->
                    response.takeIf { it.isSuccessful }?.body()?.let { user ->
                        LoadState.Data(data = user)
                    } ?: LoadState.Failure(Exception(response.errorBody().toString()))
                }
            }
        }
    }

    override suspend fun token(user: User): LoadState<String> {
        return withContext(dispatcher) {
            catchException {
                api.token(toQueryMap(user)).awaitResponse().let { response ->
                    response.takeIf { it.isSuccessful }?.body()?.let { token ->
                        LoadState.Data(data = token)
                    } ?: LoadState.Failure(Exception(response.errorBody().toString()))
                }
            }
        }
    }


    private suspend fun <T> catchException(function: suspend (() -> LoadState<T>)): LoadState<T> {
        return try {
            function()
        } catch (e: IOException) {
            Timber.e(e)
            LoadState.Failure(Exception(e))
        }
    }
}