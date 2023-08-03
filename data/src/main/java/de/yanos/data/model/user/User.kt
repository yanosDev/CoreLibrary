package de.yanos.data.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class User(
    @PrimaryKey var id: String,
    var firstName: String,
    var lastName: String,
    var password: String
)