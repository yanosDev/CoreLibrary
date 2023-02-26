package de.yanos.chat.domain.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import de.yanos.chat.data.Message
import de.yanos.chat.domain.usecase.PaginateMessagesUseCase
import de.yanos.firestorewrapper.domain.PageKey
import de.yanos.firestorewrapper.domain.StoreResult
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.suspendCoroutine

/**
 * This implementation won't be perfect but the general rules will be:
 * 1. We never delete Messages only update them
 * 2. Updates will be refreshed immediately in room only for the recent 2 weeks
 * 3.
 */
@OptIn(ExperimentalPagingApi::class)
internal class MessageMediator(
    private val chatId: String,
    private val useCase: PaginateMessagesUseCase,
) : RemoteMediator<Int, Message>() {

    private var lastkey: MutableList<PageKey> = mutableListOf<PageKey>()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Message>
    ): MediatorResult {
        return try {
            var isPreviousLoads = false
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> lastkey.lastOrNull()
            }
            val response =
                useCase.paginateMessages(
                    chatId = chatId,
                    reference = loadKey,
                    isPreviousLoads = isPreviousLoads,
                    limit = state.config.pageSize.toLong()
                )

            (response as? StoreResult.Load)?.data?.let { (messages, key) ->
                lastkey.add(key)
                useCase.updateLocalMessages(chatId, loadType == LoadType.REFRESH, messages)
            }
            MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}