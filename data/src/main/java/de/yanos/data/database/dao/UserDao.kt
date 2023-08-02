package de.yanos.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.data.model.user.User

@Dao
interface UserDao : BaseDao<User> {

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun loadUser(id: String): User?
}