package de.yanos.chat.domain.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import de.yanos.chat.data.Message
import de.yanos.chat.domain.usecase.PaginateMessagesUseCase
import de.yanos.firestorewrapper.domain.PageKey
import de.yanos.firestorewrapper.domain.StoreResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPagingApi::class)
internal class MessageMediator(
    private val chatId: String,
    private val useCase: PaginateMessagesUseCase,
    private val invalidateCallback: () -> Unit
) : RemoteMediator<Int, Message>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Message>
    ): MediatorResult {
        return try {
            var isPreviousLoads = false
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.APPEND -> state.lastItemOrNull()?.ts
                LoadType.PREPEND -> {
                    isPreviousLoads = true
                    state.firstItemOrNull()?.ts ?: return MediatorResult.Success(endOfPaginationReached = false)
                }
            }

            val response = useCase.paginateMessages(
                chatId = chatId,
                key = loadKey,
                isPreviousLoads = isPreviousLoads,
                limit = state.config.pageSize.toLong()
            )
            response.data.let { (messages, key) ->
                useCase.updateLocalMessages(chatId, loadType == LoadType.REFRESH, messages)
                MediatorResult.Success(endOfPaginationReached = messages.isEmpty())
            }

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}