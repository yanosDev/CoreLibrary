@file:OptIn(ExperimentalPagingApi::class)

package de.yanos.chat.domain.usecase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.RoomDatabase
import de.yanos.chat.data.Message
import de.yanos.chat.domain.database.MessageDao
import de.yanos.chat.domain.repository.MessageCreationContent
import de.yanos.chat.domain.repository.MessageRepository
import de.yanos.chat.domain.source.MessageMediator
import de.yanos.firestorewrapper.domain.StoreResult
import kotlinx.coroutines.flow.Flow

interface PaginateMessagesUseCase {
    fun getMessagePageData(chatId: String): Flow<PagingData<Message>>
    suspend fun createMessage(message: MessageCreationContent): StoreResult<Message>
    suspend fun messagesHaveChanged(chatId: String): Flow<StoreResult<List<Message>>>

    suspend fun paginateMessages(
        chatId: String,
        refMsg: Message?,
        isPreviousLoads: Boolean,
        limit: Long
    ): StoreResult.Load<List<Message>>
}

@ExperimentalPagingApi
internal class PaginateMessagesUseCaseImpl(
    private val database: RoomDatabase,
    private val messageDao: MessageDao,
    private val messageRepository: MessageRepository,
) : PaginateMessagesUseCase {

    override suspend fun paginateMessages(
        chatId: String,
        refMsg: Message?,
        isPreviousLoads: Boolean,
        limit: Long
    ): StoreResult.Load<List<Message>> {
        return messageRepository.getMessagePage(chatId = chatId, refMsg = refMsg, reverseOrder = isPreviousLoads, limit = limit)
    }

    override suspend fun messagesHaveChanged(chatId: String): Flow<StoreResult<List<Message>>> {
        return messageRepository.listenToChanges(chatId)
    }

    override fun getMessagePageData(chatId: String): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 3, enablePlaceholders = false),
            remoteMediator = MessageMediator(chatId, messageDao, messageRepository)
        ) {
            messageDao.pagingSource(chatId)
        }.flow
    }

    override suspend fun createMessage(message: MessageCreationContent): StoreResult<Message> {
        return messageRepository.createMessage(message)
    }


}