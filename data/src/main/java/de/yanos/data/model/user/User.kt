package de.yanos.data.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class User(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String,
    val cryptPwd: String
)