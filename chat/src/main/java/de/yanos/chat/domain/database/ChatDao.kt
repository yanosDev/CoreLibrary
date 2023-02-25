package de.yanos.chat.domain.database

import androidx.room.Dao
import de.yanos.chat.data.Chat
import de.yanos.corelibrary.base.BaseDao

@Dao
interface ChatDao : BaseDao<Chat> {
}