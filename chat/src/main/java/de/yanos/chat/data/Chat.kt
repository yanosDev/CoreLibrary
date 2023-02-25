package de.yanos.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat")
data class Chat(
    @PrimaryKey(autoGenerate = false) val id: String = "",
    val name: String = "",
    val memberIds: List<String> = listOf(),
    val previousMemberIds: List<String> = listOf()
)