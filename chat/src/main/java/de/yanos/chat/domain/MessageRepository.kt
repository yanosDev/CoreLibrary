package de.yanos.chat.domain

import de.yanos.chat.data.Message
import de.yanos.chat.data.MessageState
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

private data class MessageRepositoryConfig(
    var dispatcher: CoroutineDispatcher = Dispatchers.IO,
    var databaseRepository: DatabaseRepository = DatabaseRepositoryBuilder.Builder().setDispatcher(dispatcher).build()
)

interface MessageRepositoryBuilder {
    fun setDatabaseRepository(databaseRepository: DatabaseRepository): MessageRepositoryBuilder
    fun setDispatcher(dispatcher: CoroutineDispatcher): MessageRepositoryBuilder
    fun build(): MessageRepository

    companion object {
        fun Builder(): MessageRepositoryBuilder {
            return MessageRepositoryBuilderImpl()
        }
    }
}

private class MessageRepositoryBuilderImpl : MessageRepositoryBuilder {
    private val config: MessageRepositoryConfig = MessageRepositoryConfig()
    override fun setDatabaseRepository(databaseRepository: DatabaseRepository): MessageRepositoryBuilder {
        config.databaseRepository = databaseRepository
        return this
    }

    override fun setDispatcher(dispatcher: CoroutineDispatcher): MessageRepositoryBuilder {
        config.dispatcher = dispatcher
        return this
    }

    override fun build(): MessageRepository {
        return MessageRepositoryImpl(config)
    }
}

sealed interface MessageCreationContent {
    val ts: Long
    val id: String
    val chatId: String
    val creatorId: String
    val refMsgId: String?

    fun toMap(): Map<String, Any> {
        return mapOf(
            "ts" to ts,
            "id" to id,
            "chatId" to chatId,
            "creatorId" to creatorId,
            "refMsgId" to (refMsgId ?: FieldEdit.Delete),
            "state" to mutableMapOf<String, MessageState>().apply { put(creatorId, MessageState.SENT) },
            "reactions" to mutableMapOf<String, List<String>>().apply { put(creatorId, mutableListOf()) }
        )
    }
}

data class TextMessageCreationContent(
    override val id: String,
    override val chatId: String,
    override val creatorId: String,
    override val ts: Long,
    override val refMsgId: String?,
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
    suspend fun updateMessageText(id: String, userId: String, text: String): StoreResult<Message>
    suspend fun updateMessageState(id: String, userId: String, state: MessageState): StoreResult<Message>
    suspend fun addMessageReaction(id: String, userId: String, reactions: String): StoreResult<Message>
    suspend fun removeMessageReaction(id: String, userId: String, reaction: String): StoreResult<Message>
}

private class MessageRepositoryImpl(
    config: MessageRepositoryConfig
) : MessageRepository {
    private val dispatcher = config.dispatcher
    private val databaseRepository = config.databaseRepository

    private fun collectionPath(): CollectionPathBuilder<Message> {
        return DatabasePathBuilder.Builder("messages", Message::class.java)
    }

    private fun documentPath(id: String): DocumentPathBuilder<Message> {
        return collectionPath().document(id)
    }

    override suspend fun createMessage(content: MessageCreationContent): StoreResult<Message> {
        return withContext(dispatcher) {
            databaseRepository.create(
                documentPath(content.id).build(),
                content.toMap()
            )
        }
    }

    override suspend fun updateMessageText(id: String, userId: String, text: String): StoreResult<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMessageState(id: String, userId: String, state: MessageState): StoreResult<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun addMessageReaction(id: String, userId: String, reactions: String): StoreResult<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun removeMessageReaction(id: String, userId: String, reaction: String): StoreResult<Message> {
        TODO("Not yet implemented")
    }
}