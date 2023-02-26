package de.yanos.libraries.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import de.yanos.chat.domain.api.ChatApiBuilder
import de.yanos.core.ui.view.MessageBox
import de.yanos.core.ui.view.MessageBoxEvent
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatView(
    modifier: Modifier = Modifier,
    chatId: String = "easDopkS1taIDJqdqjxA"
) {
    val scope = rememberCoroutineScope()
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
    Column {
        LazyColumn(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = WindowInsets.statusBars.add(WindowInsets(top = 48.dp)).asPaddingValues(),
            reverseLayout = true
        ) {
            items(
                items = messages,
                key = { it.id }
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
            when (val state = messages.loadState.prepend) { // Pagination
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
                            Text(text = "Prepend in progress")

                            CircularProgressIndicator(color = Color.Black)
                        }
                    }
                }
                else -> {}
            }
        }
        MessageBox { event: MessageBoxEvent ->
            scope.launch {
                when (event) {
                    MessageBoxEvent.ResetScroll -> {}
                    is MessageBoxEvent.SendTestMessage -> viewModel.createNewMessage(event.text, messages.itemCount)
                }
            }
        }
    }
}