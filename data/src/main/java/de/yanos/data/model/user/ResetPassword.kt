package de.yanos.data.model.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ResetPassword(private val email: String, private val pwd: String)