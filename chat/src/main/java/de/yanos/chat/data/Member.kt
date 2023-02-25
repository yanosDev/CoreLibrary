package de.yanos.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("member")
data class Member(
    @PrimaryKey(autoGenerate = false) val id: String = "",
    val state: ChatState = ChatState.ACTIVE
)

enum class ChatState {
    ACTIVE, FAVOURITE, MUTED, INACTIVE
}