package de.yanos.chat.domain.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import de.yanos.chat.data.Message
import de.yanos.chat.domain.usecase.PaginateMessagesUseCase
import de.yanos.firestorewrapper.domain.StoreResult

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

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Message>
    ): MediatorResult {
        return try {
            var isPreviousLoads = false
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> {
                    val firstItem =
                        state.anchorPosition
                            ?.let { state.closestPageToPosition(it) }
                            ?.data
                            ?.firstOrNull()
                            ?: state.firstItemOrNull() ?: return MediatorResult.Success(
                                endOfPaginationReached = true
                            )
                    isPreviousLoads = true
                    firstItem
                }
                LoadType.APPEND -> {
                    val lastItem =
                        state.anchorPosition
                            ?.let { state.closestPageToPosition(it) }
                            ?.data
                            ?.lastOrNull()
                            ?: state.lastItemOrNull() ?: return MediatorResult.Success(
                                endOfPaginationReached = true
                            )
                    isPreviousLoads = false
                    lastItem
                }
            }
            val response =
                useCase.loadMessages(
                    chatId = chatId,
                    reference = loadKey,
                    isPreviousLoads = isPreviousLoads,
                    limit = state.config.pageSize.toLong()
                )
            useCase.updateLocalMessages(chatId, loadType == LoadType.REFRESH, response)
            MediatorResult.Success(endOfPaginationReached = (response as? StoreResult.Load)?.data?.isEmpty() ?: true)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}