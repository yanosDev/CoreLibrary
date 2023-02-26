package de.yanos.chat.domain.repository

import de.yanos.chat.data.Message
import de.yanos.chat.data.MessageState
import de.yanos.firestorewrapper.domain.DatabaseRepository
import de.yanos.firestorewrapper.domain.DatabaseRepositoryBuilder
import de.yanos.firestorewrapper.domain.StoreResult
import de.yanos.firestorewrapper.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

sealed interface MessageRepositoryBuilder {
    fun setDatabaseRepository(databaseRepository: DatabaseRepository): MessageRepositoryBuilder
    fun setDispatcher(dispatcher: CoroutineDispatcher): MessageRepositoryBuilder
    fun build(): MessageRepository

    companion object {
        fun builder(): MessageRepositoryBuilder {
            return MessageRepositoryBuilderImpl()
        }
    }
}

private class MessageRepositoryBuilderImpl : MessageRepositoryBuilder {
    var dispatcher: CoroutineDispatcher? = null
    var databaseRepository: DatabaseRepository? = null
    override fun setDatabaseRepository(databaseRepository: DatabaseRepository): MessageRepositoryBuilder {
        this.databaseRepository = databaseRepository
        return this
    }

    override fun setDispatcher(dispatcher: CoroutineDispatcher): MessageRepositoryBuilder {
        this.dispatcher = dispatcher
        return this
    }

    override fun build(): MessageRepository {
        return MessageRepositoryImpl(databaseRepository, dispatcher)
    }
}

sealed interface MessageCreationContent {
    val ts: Long
    val id: String
    val chatId: String
    val creatorId: String
    val refMsgId: String?

    fun toMap(): Map<String, Any> {
        return mutableMapOf(
            "ts" to ts,
            "id" to id,
            "chatId" to chatId,
            "creatorId" to creatorId,
            "state" to mutableMapOf<String, MessageState>().apply { put(creatorId, MessageState.SENT) },
            "reactions" to mutableMapOf<String, List<String>>().apply { put(creatorId, mutableListOf()) }
        ).apply {
            refMsgId?.let { ref -> put("refMsgId", ref) }
        }
    }
}

data class TextMessageCreationContent(
    override val id: String,
    override val chatId: String,
    override val creatorId: String,
    override val ts: Long,
    override val refMsgId: String? = null,
    val text: String,
) : MessageCreationContent {
    override fun toMap(): Map<String, Any> {
        return super.toMap().toMutableMap().apply {
            put("text", text)
        }
    }
}

data class MediaMessageCreationContent(
    override val id: String,
    override val chatId: String,
    override val creatorId: String,
    override val ts: Long,
    override val refMsgId: String?,
    val mediaId: String,
    val mediaName: String,
    val mimeType: String,
    val mediaPath: String,
    val size: Long
) : MessageCreationContent {
    override fun toMap(): Map<String, Any> {
        return super.toMap().toMutableMap().apply {
            put(
                "media",
                mapOf(
                    "id" to mediaId,
                    "name" to mediaName,
                    "mimeType" to mimeType,
                    "path" to mediaPath,
                    "size" to size
                )
            )
        }
    }
}

interface MessageRepository {
    suspend fun createMessage(content: MessageCreationContent): StoreResult<Message>
    suspend fun updateMessageText(id: String, chatId: String, text: String): StoreResult<Message>
    suspend fun updateMessageState(id: String, chatId: String, userId: String, state: MessageState): StoreResult<Message>
    suspend fun addMessageReaction(id: String, chatId: String, userId: String, reaction: String): StoreResult<Message>
    suspend fun removeMessageReaction(id: String, chatId: String, userId: String, reaction: String): StoreResult<Message>
    suspend fun loadMessages(
        chatId: String,
        key: Long?,
        isPreviousLoads: Boolean,
        limit: Long
    ): StoreResult.Load<Pair<List<Message>, PageKey>>

    suspend fun listenToChanges(chatId: String): Flow<StoreResult<List<Message>>>
}

private class MessageRepositoryImpl(
    dr: DatabaseRepository?,
    cd: CoroutineDispatcher?
) : MessageRepository {
    private val dispatcher = cd ?: Dispatchers.IO
    private val databaseRepository = dr ?: DatabaseRepositoryBuilder.builder()
        .setDispatcher(dispatcher)
        .enableOfflinePersistence().build()

    private fun collectionPath(chatId: String): CollectionPathBuilder<Message> {
        return DatabasePathBuilder
            .Builder("chats", Message::class.java)
            .document(chatId)
            .collection("messages")
    }

    private fun documentPath(chatId: String, id: String): DocumentPathBuilder<Message> {
        return collectionPath(chatId).document(id)
    }

    override suspend fun createMessage(content: MessageCreationContent): StoreResult<Message> {
        return withContext(dispatcher) {
            databaseRepository.create(
                documentPath(content.chatId, content.id).build(),
                content.toMap()
            )
        }
    }

    override suspend fun updateMessageText(id: String, chatId: String, text: String): StoreResult<Message> {
        return withContext(dispatcher) {
            databaseRepository.update(
                documentPath(chatId, id).build(),
                mapOf("text" to text)
            )
        }
    }

    override suspend fun updateMessageState(id: String, chatId: String, userId: String, state: MessageState): StoreResult<Message> {
        return withContext(dispatcher) {
            databaseRepository.update(
                documentPath(chatId, id).build(),
                mapOf("state.$userId" to state)
            )
        }
    }

    override suspend fun addMessageReaction(id: String, chatId: String, userId: String, reaction: String): StoreResult<Message> {
        return withContext(dispatcher) {
            databaseRepository.update(
                documentPath(chatId, id).build(),
                mapOf("state.$userId" to FieldEdit.ArrayAdd(listOf(reaction)))
            )
        }
    }

    override suspend fun removeMessageReaction(id: String, chatId: String, userId: String, reaction: String): StoreResult<Message> {
        return withContext(dispatcher) {
            databaseRepository.update(
                documentPath(chatId, id).build(),
                mapOf("state.$userId" to FieldEdit.ArrayRemove(listOf(reaction)))
            )
        }
    }

    override suspend fun loadMessages(
        chatId: String,
        key: Long?,
        isPreviousLoads: Boolean,
        limit: Long
    ): StoreResult.Load<Pair<List<Message>, PageKey>> {
        return withContext(dispatcher) {
            databaseRepository.paginateList(collectionPath(chatId), key, isPreviousLoads, "ts", limit)
        }
    }

    override suspend fun listenToChanges(chatId: String): Flow<StoreResult<List<Message>>> {
        return withContext(dispatcher) {
            databaseRepository.subscribeChanges(
                collectionPath(chatId).condition(Condition.OrderByDescending("ts"))
                    .condition(
                        Condition.Limit(1000)
                    ).build()
            )
        }
    }
}