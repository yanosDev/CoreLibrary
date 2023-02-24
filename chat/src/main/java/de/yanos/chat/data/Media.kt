package de.yanos.chat.data

data class Media(
    val id: String,
    val name: String,
    val mimeType: String,
    val path: String,
    val size: Long
)