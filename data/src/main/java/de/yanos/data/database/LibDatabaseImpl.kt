package de.yanos.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.yanos.data.database.dao.UserDao
import de.yanos.data.model.user.User

@Database(
    entities = [User::class],
    version = 0
)
internal abstract class LibDatabaseImpl : RoomDatabase(), LibDatabase {
    abstract override fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: LibDatabase? = null

        fun db(context: Context): LibDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, LibDatabaseImpl::class.java, "lib_db")
                    .build()
            }.also { INSTANCE = it }
        }
    }

}