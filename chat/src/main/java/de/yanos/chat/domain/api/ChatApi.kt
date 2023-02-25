package de.yanos.chat.domain.api

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import de.yanos.chat.data.Message
import de.yanos.chat.domain.database.ChatDatabase
import de.yanos.chat.domain.repository.*
import de.yanos.chat.domain.source.MessageMediator
import de.yanos.chat.domain.usecase.PaginateMessagesUseCase
import de.yanos.chat.domain.usecase.PaginateMessagesUseCaseImpl
import de.yanos.firestorewrapper.domain.DatabaseRepository
import de.yanos.firestorewrapper.domain.DatabaseRepositoryBuilder
import de.yanos.firestorewrapper.domain.StoreResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

interface ChatApi {
    fun getMessageFlow(chatId: String): Flow<PagingData<Message>>
}

@ExperimentalPagingApi
internal class ChatApiImpl(
    ctx: Context,
    dr: DatabaseRepository?,
    cd: CoroutineDispatcher?
) : ChatApi {
    private val memberRepository: MemberRepository
    private val chatRepository: ChatRepository
    private val database: ChatDatabase
    private val messageRepository: MessageRepository
    private val pageUseCase: PaginateMessagesUseCase

    init {
        val dispatcher = cd ?: Dispatchers.IO
        val databaseRepository =
            dr
                ?: DatabaseRepositoryBuilder.Builder()
                    .setDispatcher(dispatcher)
                    .enableOfflinePersistence().build()
        chatRepository = ChatRepositoryBuilder
            .Builder()
            .setDispatcher(dispatcher)
            .setDatabaseRepository(databaseRepository)
            .build()
        messageRepository = MessageRepositoryBuilder
            .Builder()
            .setDispatcher(dispatcher)
            .setDatabaseRepository(databaseRepository)
            .build()
        memberRepository = MemberRepositoryBuilder
            .Builder()
            .setDispatcher(dispatcher)
            .setDatabaseRepository(databaseRepository)
            .build()
        database = ChatDatabase.getInstance(ctx)
        pageUseCase = PaginateMessagesUseCaseImpl(database, database.messageDao(), messageRepository)
    }

    override fun getMessageFlow(chatId: String): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(pageSize = 50),
            remoteMediator = MessageMediator("", pageUseCase)
        ) {
            database.messageDao().pagingSource("")
        }.flow
    }
}