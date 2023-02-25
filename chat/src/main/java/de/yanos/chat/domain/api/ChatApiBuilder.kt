package de.yanos.chat.domain.api

import android.content.Context
import de.yanos.firestorewrapper.domain.DatabaseRepository
import kotlinx.coroutines.CoroutineDispatcher


interface ChatApiBuilder {
    fun setDatabaseRepository(databaseRepository: DatabaseRepository): ChatApiBuilder
    fun setDispatcher(dispatcher: CoroutineDispatcher): ChatApiBuilder
    fun build(ctx: Context): ChatApi
}

private class ChatApiBuilderImpl() : ChatApiBuilder {
    private var databaseRepository: DatabaseRepository? = null
    private var dispatcher: CoroutineDispatcher? = null

    override fun setDatabaseRepository(databaseRepository: DatabaseRepository): ChatApiBuilder {
        this.databaseRepository = databaseRepository
        return this
    }

    override fun setDispatcher(dispatcher: CoroutineDispatcher): ChatApiBuilder {
        this.dispatcher = dispatcher
        return this
    }

    override fun build(ctx: Context): ChatApi {
        return ChatApiImpl(ctx, databaseRepository, dispatcher)
    }
}