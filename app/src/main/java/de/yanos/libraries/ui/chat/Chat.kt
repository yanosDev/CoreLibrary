package de.yanos.libraries.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SendToMobile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
        ChatApiBuilder.Builder().build(
            LocalContext.current.applicationContext
        )
    )
    val messages = viewModel.messages.collectAsLazyPagingItems()
    val executor = { msg: String -> scope.launch { viewModel.createNewMessage(msg, messages.itemCount) } }
    val refreshExecutor = { scope.launch { messages.refresh() } }
    viewModel.refreshCallback {
        refreshExecutor()
    }


    LazyColumn(modifier = modifier, reverseLayout = true) {
        stickyHeader {
            Row(modifier = Modifier.fillMaxWidth()) {
                val msgValue = remember { mutableStateOf("") }
                TextField(value = msgValue.value, onValueChange = { newValue -> msgValue.value = newValue })
                IconButton(onClick = { executor(msgValue.value) }) { Icon(Icons.Rounded.SendToMobile, contentDescription = "") }
            }
        }

        items(
            items = messages,
            key = { it.id }
        ) { message ->
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .height(75.dp),
                text = message?.text ?: "Placeholder",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Divider()
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
}