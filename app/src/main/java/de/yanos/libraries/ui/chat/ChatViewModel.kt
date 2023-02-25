package de.yanos.libraries.ui.chat

import androidx.lifecycle.ViewModel
import de.yanos.chat.domain.api.ChatApi
import de.yanos.chat.domain.usecase.PaginateMessagesUseCase

class ChatViewModel(chatId: String, private val chatApi: ChatApi) : ViewModel() {


    init {
        val chat = chatApi.getMessageFlow(chatId)
    }
}