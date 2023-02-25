package de.yanos.chat.domain.repository

import de.yanos.chat.data.Chat
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
    var dispatcher: CoroutineDispatcher? = null
    var databaseRepository: DatabaseRepository? = null
    override fun setDatabaseRepository(databaseRepository: DatabaseRepository): ChatRepositoryBuilder {
        this.databaseRepository = databaseRepository
        return this
    }

    override fun setDispatcher(dispatcher: CoroutineDispatcher): ChatRepositoryBuilder {
        this.dispatcher = dispatcher
        return this
    }

    override fun build(): ChatRepository {
        return ChatRepositoryImpl(databaseRepository, dispatcher)
    }
}

interface ChatRepository {
    suspend fun createChat(id: String, members: List<String>): StoreResult<Chat>
    suspend fun readChat(id: String): StoreResult<Chat>
    suspend fun addChatMembers(id: String, newMembers: List<String>): StoreResult<Chat>
    suspend fun removeChatMembers(id: String, removedMembers: List<String>): StoreResult<Chat>
    suspend fun updateChatName(id: String, name: String): StoreResult<Chat>
}

private class ChatRepositoryImpl(
    dr: DatabaseRepository?,
    cd: CoroutineDispatcher?
) : ChatRepository {
    private val dispatcher = cd ?: Dispatchers.IO
    private val databaseRepository = dr ?: DatabaseRepositoryBuilder.Builder()
        .setDispatcher(dispatcher)
        .enableOfflinePersistence().build()

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
                    "memberIds" to members,
                    "previousMemberIds" to listOf<String>()
                )
            )
        }
    }

    override suspend fun readChat(id: String): StoreResult<Chat> {
        return withContext(dispatcher) {
            databaseRepository.read(documentPath(id).build())
        }
    }

    override suspend fun addChatMembers(id: String, newMembers: List<String>): StoreResult<Chat> {
        return withContext(dispatcher) {
            databaseRepository.update(
                documentPath(id).build(),
                mapOf(
                    "memberIds" to FieldEdit.ArrayAdd(newMembers),
                    "previousMemberIds" to FieldEdit.ArrayRemove(newMembers)
                )
            )
        }
    }

    override suspend fun removeChatMembers(id: String, removedMembers: List<String>): StoreResult<Chat> {
        return withContext(dispatcher) {
            databaseRepository.update(
                documentPath(id).build(),
                mapOf(
                    "memberIds" to FieldEdit.ArrayRemove(removedMembers),
                    "previousMemberIds" to FieldEdit.ArrayAdd(removedMembers)
                )
            )
        }
    }

    override suspend fun updateChatName(id: String, name: String): StoreResult<Chat> {
        return withContext(dispatcher) {
            databaseRepository.update(documentPath(id).build(), mapOf("name" to name))
        }
    }
}