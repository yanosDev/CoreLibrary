package de.yanos.libraries.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import de.yanos.chat.domain.api.ChatApiBuilder


@Composable
fun ChatView(
    modifier: Modifier = Modifier,
    chatId: String
) {
    val viewModel = ChatViewModel(
        chatId = chatId,
        ChatApiBuilder.Builder().build(
            LocalContext.current.applicationContext
        )
    )
}