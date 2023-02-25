package de.yanos.chat.domain.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import de.yanos.chat.data.Message
import de.yanos.corelibrary.base.BaseDao

@Dao
interface MessageDao : BaseDao<Message> {
    @Query("SELECT * FROM messages WHERE chatId LIKE :chatId ORDER BY ts DESC")
    fun pagingSource(chatId: String): PagingSource<Int, Message>

    @Query("DELETE FROM messages WHERE chatId LIKE :chatId")
    fun deleteByChatId(chatId: String)
}