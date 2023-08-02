package de.yanos.data.service.auth

import de.yanos.core.utils.IODispatcher
import de.yanos.data.database.dao.UserDao
import de.yanos.data.model.user.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AuthLocalSourceImpl @Inject constructor(
    private val dao: UserDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : AuthLocalSource {
    override suspend fun saveUser(user: User) {
        withContext(dispatcher) {
            dao.insert(user)
        }
    }

    override suspend fun loadUser(id: String): User? {
        return withContext(dispatcher) { dao.loadUser(id) }
    }
}