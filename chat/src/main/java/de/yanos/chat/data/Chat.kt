package de.yanos.chat.data

data class Chat(
    val id: String,
    val name: String,
    val memberIds: List<String>,
    val previousMemberIds: List<String>
)