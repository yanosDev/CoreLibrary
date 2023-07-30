package de.yanos.data.service.auth

import de.yanos.data.database.dao.UserDao
import de.yanos.data.model.user.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class AuthLocalSourceImpl(
    private val dao: UserDao,
    private val dispatcher: CoroutineDispatcher
) : AuthLocalSource {
    override suspend fun saveUser(user: User) {
        withContext(dispatcher) {
            dao.insert(user)
        }
    }

    override suspend fun loadUser(id: String): User {
        return withContext(dispatcher) { dao.loadUser(id) }
    }
}