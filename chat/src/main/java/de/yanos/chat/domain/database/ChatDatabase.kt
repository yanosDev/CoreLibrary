package de.yanos.chat.domain.database

import android.content.Context
import android.telecom.Call
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import de.yanos.chat.data.Chat
import de.yanos.chat.data.Member
import de.yanos.chat.data.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(entities = [Chat::class, Message::class, Member::class], version = 1)
internal abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun memberDao(): MemberDao

    companion object {
        private var instance: ChatDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): ChatDatabase {
            return instance ?: Room
                .databaseBuilder(ctx.applicationContext, ChatDatabase::class.java, "chat_db")
                .fallbackToDestructiveMigration()
                .build().also { instance = it }
        }
    }
}