package de.yanos.chat.data


data class Member(val id: String, val state: ChatState)

enum class ChatState {
    ACTIVE, FAVOURITE, MUTED, INACTIVE
}