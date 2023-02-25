package de.yanos.libraries.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import de.yanos.chat.data.Message
import de.yanos.chat.domain.api.ChatApi
import de.yanos.chat.domain.repository.ChatRepositoryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(chatId: String, private val chatApi: ChatApi) : ViewModel() {
    val messages: Flow<PagingData<Message>> = chatApi.getMessageFlow(chatId).cachedIn(viewModelScope)

    init {

        viewModelScope.launch {
            //ChatRepositoryBuilder.Builder().build().readChat(id = "5406ea1a-8128-48b7-9747-09e35a8e280b")
            ChatRepositoryBuilder.Builder().build().createChat(id = UUID.randomUUID().toString(), listOf("U1MBPMY4JjNEwyDQ8DahTCvxY6z1"))

            /*
                       (0..100).forEach {
                           chatApi.createMessage(
                               TextMessageCreationContent(
                                   id = UUID.randomUUID().toString(),
                                   chatId = chatId,
                                   creatorId = "U1MBPMY4JjNEwyDQ8DahTCvxY6z1",
                                   ts = System.currentTimeMillis(),
                                   text = it.toString()
                               )
                           )
                       }
            */
        }
    }
}