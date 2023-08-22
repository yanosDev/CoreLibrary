package de.yanos.domain.data

data class UserDto(val id: String, val password: String?, val firstName: String? = null, val lastName: String? = null)