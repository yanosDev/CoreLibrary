package de.yanos.chat.data


data class Message(
    val id: String,
    val chatId: String,
    val creatorId: String,
    val text: String?,
    val media: Media,
    val refMsgId: String?,
    val state: Map<String, MessageState>,
    val reactions: Map<String, List<String>>,
    val ts: Long,
)

enum class MessageState {
    SENT, DELIVERED, READ, DELETED
}