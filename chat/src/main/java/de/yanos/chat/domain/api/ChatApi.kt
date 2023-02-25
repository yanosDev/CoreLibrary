package de.yanos.chat.domain.api

import android.content.Context
import de.yanos.chat.domain.database.ChatDatabase
import de.yanos.chat.domain.repository.*
import de.yanos.firestorewrapper.domain.DatabaseRepository
import de.yanos.firestorewrapper.domain.DatabaseRepositoryBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface ChatApi {

}

internal class ChatApiImpl(
    ctx: Context,
    dr: DatabaseRepository?,
    cd: CoroutineDispatcher?
) : ChatApi {
    private val messageRepository: MessageRepository
    private val memberRepository: MemberRepository
    private val chatRepository: ChatRepository
    private val database: ChatDatabase


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
    }
}