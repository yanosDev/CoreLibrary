package de.yanos.libraries.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import de.yanos.chat.data.Message
import de.yanos.chat.domain.repository.TextMessageCreationContent
import de.yanos.chat.domain.usecase.PaginateMessagesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(val chatId: String, val useCase: PaginateMessagesUseCase) : ViewModel() {
    val messages: Flow<PagingData<Message>> = useCase.getMessagePageData(chatId).cachedIn(viewModelScope)
    private var task: Job? = null
    fun refreshCallback(function: () -> Job) {
        if (task == null)
            task = viewModelScope.launch {
                useCase.messagesHaveChanged(chatId).distinctUntilChanged().collectLatest { result ->
                    /*if ((result as? StoreResult.Load)?.data?.isNotEmpty() == true)
                        function()*/
                }
            }
    }

    fun createNewMessage(value: String, itemCount: Int) {
        viewModelScope.launch {
            useCase.createMessage(
                TextMessageCreationContent(
                    id = UUID.randomUUID().toString(),
                    text = value,
                    createdAt = System.currentTimeMillis(),
                    chatId = chatId,
                    creatorId = "U1MBPMY4JjNEwyDQ8DahTCvxY6z1",
                )
            )
        }
    }
}