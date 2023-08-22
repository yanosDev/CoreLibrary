package de.yanos.data.database

import de.yanos.data.database.dao.UserDao

interface LibDatabase {
    fun userDao(): UserDao
}