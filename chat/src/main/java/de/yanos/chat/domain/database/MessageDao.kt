package de.yanos.chat.domain.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import de.yanos.chat.data.Message
import de.yanos.corelibrary.base.BaseDao

@Dao
interface MessageDao : BaseDao<Message> {
    @Query("SELECT * FROM messages WHERE ")
    fun pagingSource(query: String): PagingSource<Int, Message>
}