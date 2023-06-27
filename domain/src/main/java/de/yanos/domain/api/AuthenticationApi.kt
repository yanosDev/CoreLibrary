package de.yanos.domain.api

import de.yanos.domain.data.UserDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface AuthenticationApi {
    @POST("auth/register")
    fun register(@Body user: UserDto): Call<UserDto>

    @POST("auth/signIn")
    fun signIn(@Body user: UserDto): Call<String>

    @POST("auth/signOut")
    fun signOut(): Call<Boolean>

    @GET("auth/isLoggedIn")
    fun getToken(@Body user: UserDto): Call<String>
}