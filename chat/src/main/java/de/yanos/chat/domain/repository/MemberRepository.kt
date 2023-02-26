package de.yanos.chat.domain.repository

import de.yanos.chat.data.ChatState
import de.yanos.chat.data.Member
import de.yanos.firestorewrapper.domain.DatabaseRepository
import de.yanos.firestorewrapper.domain.DatabaseRepositoryBuilder
import de.yanos.firestorewrapper.domain.StoreResult
import de.yanos.firestorewrapper.util.CollectionPathBuilder
import de.yanos.firestorewrapper.util.DatabasePathBuilder
import de.yanos.firestorewrapper.util.DocumentPathBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MemberRepositoryBuilder {
    fun setDatabaseRepository(databaseRepository: DatabaseRepository): MemberRepositoryBuilder
    fun setDispatcher(dispatcher: CoroutineDispatcher): MemberRepositoryBuilder
    fun build(): MemberRepository

    companion object {
        fun builder(): MemberRepositoryBuilder {
            return MemberRepositoryBuilderImpl()
        }
    }
}


private class MemberRepositoryBuilderImpl : MemberRepositoryBuilder {
    var dispatcher: CoroutineDispatcher? = null
    var databaseRepository: DatabaseRepository? = null
    override fun setDatabaseRepository(databaseRepository: DatabaseRepository): MemberRepositoryBuilder {
        this.databaseRepository = databaseRepository
        return this
    }

    override fun setDispatcher(dispatcher: CoroutineDispatcher): MemberRepositoryBuilder {
        this.dispatcher = dispatcher
        return this
    }

    override fun build(): MemberRepository {
        return MemberRepositoryImpl(databaseRepository, dispatcher)
    }
}

interface MemberRepository {
    suspend fun createMemberState(chatId: String, userId: String, state: ChatState): StoreResult<Member>
    suspend fun updateMemberState(chatId: String, userId: String, state: ChatState): StoreResult<Member>
}

private class MemberRepositoryImpl(
    dr: DatabaseRepository?,
    cd: CoroutineDispatcher?
) : MemberRepository {
    private val dispatcher = cd ?: Dispatchers.IO
    private val databaseRepository = dr ?: DatabaseRepositoryBuilder.builder()
        .setDispatcher(dispatcher)
        .enableOfflinePersistence().build()

    private fun collectionPath(chatId: String): CollectionPathBuilder<Member> {
        return DatabasePathBuilder
            .Builder("chats", Member::class.java)
            .document(chatId)
            .collection("members")
    }

    private fun documentPath(chatId: String, id: String): DocumentPathBuilder<Member> {
        return collectionPath(chatId).document(id)
    }

    override suspend fun createMemberState(chatId: String, userId: String, state: ChatState): StoreResult<Member> {
        return withContext(dispatcher) {
            databaseRepository.update(documentPath(chatId, userId).build(), mapOf("id" to userId, "state" to state))
        }
    }

    override suspend fun updateMemberState(chatId: String, userId: String, state: ChatState): StoreResult<Member> {
        return withContext(dispatcher) {
            databaseRepository.update(documentPath(chatId, userId).build(), mapOf("state" to state))
        }
    }
}