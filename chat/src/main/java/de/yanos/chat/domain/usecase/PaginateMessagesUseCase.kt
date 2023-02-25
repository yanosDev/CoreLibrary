package de.yanos.chat.domain.usecase

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import de.yanos.chat.data.Message
import de.yanos.chat.domain.database.MessageDao
import de.yanos.chat.domain.repository.MessageRepository
import de.yanos.firestorewrapper.domain.StoreResult

interface PaginateMessagesUseCase {
    suspend fun loadMessages(chatId: String, referenceId: String?, isNextLoading: Boolean, limit: Int): StoreResult<List<Message>>
    suspend fun updateLocalMessages(chatId: String, isRefresh: Boolean, result: StoreResult<List<Message>>)
}

internal class PaginateMessagesUseCaseImpl(
    private val database: RoomDatabase,
    private val messageDao: MessageDao,
    private val messageRepository: MessageRepository,
) : PaginateMessagesUseCase {

    override suspend fun loadMessages(
        chatId: String,
        referenceId: String?,
        isNextLoading: Boolean,
        limit: Int
    ): StoreResult<List<Message>> {
        return messageRepository.loadMessages(chatId = chatId, lastMessageId = referenceId, isNextLoading = isNextLoading)
    }

    override suspend fun updateLocalMessages(
        chatId: String,
        isRefresh: Boolean,
        result: StoreResult<List<Message>>
    ) {
        with(database) {
            withTransaction {
                messageDao.insert((result as? StoreResult.Load)?.data ?: return@withTransaction)
            }
        }
    }

}