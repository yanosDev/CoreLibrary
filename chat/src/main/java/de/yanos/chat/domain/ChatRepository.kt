package de.yanos.chat.domain

import de.yanos.chat.data.Chat
import de.yanos.chat.data.ChatState
import de.yanos.firestorewrapper.domain.DatabaseRepository
import de.yanos.firestorewrapper.domain.DatabaseRepositoryBuilder
import de.yanos.firestorewrapper.domain.StoreResult
import de.yanos.firestorewrapper.util.CollectionPathBuilder
import de.yanos.firestorewrapper.util.DatabasePathBuilder
import de.yanos.firestorewrapper.util.DocumentPathBuilder
import de.yanos.firestorewrapper.util.FieldEdit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class ChatRepositoryConfig(
    var dispatcher: CoroutineDispatcher = Dispatchers.IO,
    var databaseRepository: DatabaseRepository = DatabaseRepositoryBuilder.Builder().setDispatcher(dispatcher).build()
)

interface ChatRepositoryBuilder {
    fun setDatabaseRepository(databaseRepository: DatabaseRepository): ChatRepositoryBuilder
    fun setDispatcher(dispatcher: CoroutineDispatcher): ChatRepositoryBuilder
    fun build(): ChatRepository

    companion object {
        fun Builder(): ChatRepositoryBuilder {
            return ChatRepositoryBuilderImpl()
        }
    }
}

private class ChatRepositoryBuilderImpl : ChatRepositoryBuilder {
    private val config: ChatRepositoryConfig = ChatRepositoryConfig()
    override fun setDatabaseRepository(databaseRepository: DatabaseRepository): ChatRepositoryBuilder {
        config.databaseRepository = databaseRepository
        return this
    }

    override fun setDispatcher(dispatcher: CoroutineDispatcher): ChatRepositoryBuilder {
        config.dispatcher = dispatcher
        return this
    }

    override fun build(): ChatRepository {
        return ChatRepositoryImpl(config)
    }
}

interface ChatRepository {
    suspend fun createChat(id: String, members: List<String>): StoreResult<Chat>
    suspend fun addChatMembers(id: String, newMembers: List<String>): StoreResult<Chat>
    suspend fun removeChatMembers(id: String, removedMembers: List<String>): StoreResult<Chat>
    suspend fun updateChatName(id: String, name: String): StoreResult<Chat>
    suspend fun updateChatState(id: String, userId: String, state: ChatState): StoreResult<Chat>
}

private class ChatRepositoryImpl(
    config: ChatRepositoryConfig,
) : ChatRepository {
    private val dispatcher = config.dispatcher
    private val databaseRepository = config.databaseRepository

    private fun collectionPath(): CollectionPathBuilder<Chat> {
        return DatabasePathBuilder.Builder("chats", Chat::class.java)
    }

    private fun documentPath(id: String): DocumentPathBuilder<Chat> {
        return collectionPath().document(id)
    }

    override suspend fun createChat(id: String, members: List<String>): StoreResult<Chat> {
        return withContext(dispatcher) {
            databaseRepository.create(
                documentPath(id).build(),
                mapOf(
                    "id" to id,
                    "name" to "",
                    "members" to members,
                    "userStates" to mutableMapOf<String, ChatState>().apply { members.forEach { userId -> put(userId, ChatState.ACTIVE) } }
                )
            )
        }
    }

    override suspend fun addChatMembers(id: String, newMembers: List<String>): StoreResult<Chat> {
        return withContext(dispatcher) {
            databaseRepository.update(
                documentPath(id).build(),
                mapOf(
                    "members" to FieldEdit.ArrayAdd(newMembers),
                )
            )
        }
    }

    override suspend fun removeChatMembers(id: String, removedMembers: List<String>): StoreResult<Chat> {
        return withContext(dispatcher) {
            databaseRepository.update(
                documentPath(id).build(),
                mapOf(
                    "members" to FieldEdit.ArrayRemove(removedMembers),
                )
            )
        }
    }

    override suspend fun updateChatName(id: String, name: String): StoreResult<Chat> {
        return withContext(dispatcher) {
            databaseRepository.update(documentPath(id).build(), mapOf("name" to name))
        }
    }

    override suspend fun updateChatState(id: String, userId: String, state: ChatState): StoreResult<Chat> {
        return withContext(dispatcher) {
            databaseRepository.update(documentPath(id).build(), mapOf("state.$userId" to state))
        }
    }

}