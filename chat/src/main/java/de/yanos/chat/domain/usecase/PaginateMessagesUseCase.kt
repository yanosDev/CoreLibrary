package de.yanos.chat.domain.usecase

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import de.yanos.chat.data.Message
import de.yanos.chat.domain.database.MessageDao
import de.yanos.chat.domain.repository.MessageRepository
import de.yanos.firestorewrapper.domain.PageKey
import de.yanos.firestorewrapper.domain.StoreResult
import kotlinx.coroutines.flow.Flow

interface PaginateMessagesUseCase {
    suspend fun paginateMessages(
        chatId: String,
        reference: PageKey?,
        isPreviousLoads: Boolean,
        limit: Long
    ): StoreResult<Pair<List<Message>, PageKey>>

    suspend fun updateLocalMessages(chatId: String, isRefresh: Boolean, messages: List<Message>)
}

internal class PaginateMessagesUseCaseImpl(
    private val database: RoomDatabase,
    private val messageDao: MessageDao,
    private val messageRepository: MessageRepository,
) : PaginateMessagesUseCase {

    override suspend fun paginateMessages(
        chatId: String,
        key: PageKey?,
        isPreviousLoads: Boolean,
        limit: Long
    ): StoreResult<Pair<List<Message>, PageKey>> {
        return messageRepository.loadMessages(chatId = chatId, key = key, isPreviousLoads = isPreviousLoads, limit = limit)
    }

    override suspend fun updateLocalMessages(
        chatId: String,
        isRefresh: Boolean,
        messages: List<Message>
    ) {
        with(database) {
            withTransaction {
                messageDao.insert(messages)
            }
        }
    }

}