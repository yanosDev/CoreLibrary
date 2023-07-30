package de.yanos.data.model.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RegisterUserByPassword(private val email: String, private val pwd: String, private val lastName: String, private val firstName: String)