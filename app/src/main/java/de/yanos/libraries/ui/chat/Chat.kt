package de.yanos.libraries.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import de.yanos.chat.data.Message
import de.yanos.chat.domain.api.ChatApiBuilder
import de.yanos.core.ui.view.MessageBox
import de.yanos.core.ui.view.MessageBoxEvent
import kotlinx.coroutines.launch

@Composable
fun ChatView(
    modifier: Modifier = Modifier,
    chatId: String = "easDopkS1taIDJqdqjxA"
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val viewModel = ChatViewModel(
        chatId = chatId,
        ChatApiBuilder.builder().build(
            LocalContext.current.applicationContext
        ).pageUseCase
    )
    val messages = viewModel.messages.collectAsLazyPagingItems()
    val refreshExecutor = { scope.launch { messages.refresh() } }
    viewModel.refreshCallback {
        refreshExecutor()
    }
    Column(modifier = modifier) {
        Messages(modifier = Modifier.weight(1f), scrollState = scrollState, messages = messages)
        MessageBox { event: MessageBoxEvent ->
            scope.launch {
                when (event) {
                    MessageBoxEvent.ResetScroll -> {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    }

                    is MessageBoxEvent.SendTestMessage -> viewModel.createNewMessage(event.text, messages.itemCount)
                    is MessageBoxEvent.OnEmojiClicked -> {}
                }
            }
        }
    }
}

@Composable
fun Messages(
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
    messages: LazyPagingItems<Message>
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = WindowInsets.statusBars.add(WindowInsets(top = 48.dp)).asPaddingValues(),
        reverseLayout = true,
        state = scrollState
    ) {
        items(
            items = messages.itemSnapshotList,
            key = { it?.id ?: "" }
        ) { message ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(if (message?.creatorId == "") MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer)
                        .height(75.dp)
                        .align(if (message?.creatorId == "") Alignment.End else Alignment.Start),
                    text = message?.text ?: "Placeholder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        when (val state = messages.loadState.refresh) { //FIRST LOAD
            is LoadState.Error -> {
                //TODO Error Item
                //state.error to get error message
            }

            is LoadState.Loading -> { // Loading UI
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "Refresh Loading"
                        )

                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            }

            else -> {}
        }
        when (val state = messages.loadState.append) { // Pagination
            is LoadState.Error -> {
                //TODO Pagination Error Item
                //state.error to get error message
            }

            is LoadState.Loading -> { // Pagination Loading UI
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(text = "Append in progress")

                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            }

            else -> {}
        }
    }
}
