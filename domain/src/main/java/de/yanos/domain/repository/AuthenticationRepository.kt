package de.yanos.domain.repository

import android.accounts.AccountManager
import de.yanos.domain.api.AuthenticationApi
import de.yanos.domain.client.YanosClientImpl
import de.yanos.domain.data.UserDto
import retrofit2.Retrofit
import retrofit2.awaitResponse

interface AuthenticationRepositoryBuilder {
    fun addAuthenticationManager(am: AccountManager): AuthenticationRepositoryBuilder
    fun build(): AuthenticationRepository
    fun build(retrofit: Retrofit): AuthenticationRepository
}

internal class AuthenticationRepositoryBuilderImpl : AuthenticationRepositoryBuilder {
    private var am: AccountManager? = null

    override fun addAuthenticationManager(am: AccountManager): AuthenticationRepositoryBuilder {
        this.am = am
        return this
    }

    override fun build(): AuthenticationRepository {
        return AuthenticationRepositoryImpl(YanosClientImpl.default.create(AuthenticationApi::class.java), am)
    }

    override fun build(retrofit: Retrofit): AuthenticationRepository {
        return AuthenticationRepositoryImpl(retrofit.create(AuthenticationApi::class.java), am)
    }
}

interface AuthenticationRepository {
    suspend fun isLoggedIn(id: String, password: String? = null): Boolean
    suspend fun registerPasswordUser(id: String, password: String): Boolean
    suspend fun signInPasswordUser(id: String, password: String): Boolean
    suspend fun signOut(): Boolean

}

internal class AuthenticationRepositoryImpl(private val api: AuthenticationApi, private val am: AccountManager? = null) : AuthenticationRepository {
    private var token: String? = null
    override suspend fun isLoggedIn(id: String, password: String?): Boolean {
        return api.getToken(UserDto(id = id, password = password)).awaitResponse().body() != null
    }

    override suspend fun registerPasswordUser(id: String, password: String): Boolean {
        return api.register(UserDto(id = id, password = password)).awaitResponse().body()?.let {
            token = signInPasswordUser(it)
            token != null
        } ?: false
    }

    override suspend fun signInPasswordUser(id: String, password: String): Boolean {
        token = signInPasswordUser(UserDto(id = id, password = password))
        return token != null
    }

    private suspend fun signInPasswordUser(user: UserDto): String? {
        return api.signIn(user).awaitResponse().body()
    }

    override suspend fun signOut(): Boolean {
        return api.signOut().awaitResponse().body() ?: false
    }
}

