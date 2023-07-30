package de.yanos.chat.domain.database

import androidx.room.Dao
import de.yanos.chat.data.Chat
import de.yanos.data.database.dao.BaseDao

@Dao
interface ChatDao : BaseDao<Chat> {
}