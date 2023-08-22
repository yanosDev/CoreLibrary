package de.yanos.data.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import de.yanos.data.model.PostBody

@Entity
@JsonClass(generateAdapter = true)
data class User(
    @PrimaryKey var id: String,
    var firstName: String,
    var lastName: String,
    var password: String
) : PostBody {
    override fun toQueryMap(): Map<String, String> {
        return mapOf("id" to id, "firstName" to firstName, "lastName" to lastName, "password" to password)
    }
}