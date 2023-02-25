package de.yanos.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class Message(
    @PrimaryKey(autoGenerate = false) val id: String,
    val chatId: String,
    val creatorId: String,
    val text: String?,
    val media: Media?,
    val refMsgId: String?,
    val state: Map<String, MessageState>,
    val reactions: Map<String, List<String>>,
    val ts: Long,
)

data class Media(
    val id: String,
    val name: String,
    val mimeType: String,
    val path: String,
    val size: Long
)

enum class MessageState {
    SENT, DELIVERED, READ, DELETED
}