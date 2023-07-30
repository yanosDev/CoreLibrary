package de.yanos.data.database.dao

import de.yanos.data.model.user.User

interface UserDao : BaseDao<User> {

    suspend fun loadUser(id: String): User
}