package de.yanos.chat.domain.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import de.yanos.chat.data.Message
import de.yanos.chat.domain.database.MessageDao
import de.yanos.chat.domain.repository.MessageRepository
import kotlinx.coroutines.*

@OptIn(ExperimentalPagingApi::class)
internal class MessageMediator(
    private val chatId: String,
    private val messageDao: MessageDao,
    private val messageRepository: MessageRepository
) : RemoteMediator<Int, Message>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Message>
    ): MediatorResult {
        return try {
            var reverseOrder = false
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.APPEND -> state.lastItemOrNull()
                LoadType.PREPEND -> {
                    reverseOrder = true
                    state.firstItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = messageRepository.getMessagePage(
                chatId = chatId,
                refMsg = loadKey,
                reverseOrder = reverseOrder,
                limit = state.config.pageSize.toLong()
            )
            withContext(Dispatchers.IO) {
                messageDao.insert(response.data)
            }
            MediatorResult.Success(endOfPaginationReached = response.data.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}