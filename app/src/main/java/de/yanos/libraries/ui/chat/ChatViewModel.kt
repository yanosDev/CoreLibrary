package de.yanos.libraries.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import de.yanos.chat.data.Message
import de.yanos.chat.domain.api.ChatApi
import kotlinx.coroutines.flow.Flow

class ChatViewModel(chatId: String, val chatApi: ChatApi) : ViewModel() {
    val messages: Flow<PagingData<Message>> = chatApi.getMessageFlow(chatId).cachedIn(viewModelScope)
}