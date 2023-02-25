package de.yanos.chat.domain.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.yanos.chat.data.Chat
import de.yanos.chat.data.Member
import de.yanos.chat.data.Message

interface ChatDatabaseBuilder {

}

private

@Database(entities = [Chat::class, Message::class, Member::class], version = 1)
internal abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun memberDao(): MemberDao
}