package de.yanos.chat.data

data class Chat(
    val id: String,
    val name: String,
    val members: List<String>,
    val userStates: Map<String, ChatState>,
)

enum class ChatState {
    ACTIVE, FAVOURITE, MUTED, INACTIVE
}