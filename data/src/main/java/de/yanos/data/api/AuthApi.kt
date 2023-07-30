package de.yanos.data.api

import de.yanos.data.model.user.RegisterUserByPassword
import de.yanos.data.model.user.ResetPassword
import de.yanos.data.model.user.User
import de.yanos.data.model.user.UserSignIn
import de.yanos.data.model.user.UserSignInGoogle
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

internal interface AuthApi {
    @POST("auth/register")
    fun register(@Body user: RegisterUserByPassword): Call<User>

    @POST("auth/signIn")
    fun signIn(@Body user: UserSignIn): Call<User>

    @POST("auth/signInGoogle")
    fun signInGoogle(@Body user: UserSignInGoogle): Call<User>

    @POST("auth/signOut")
    fun signOut(): Call<Boolean>

    @POST("auth/resetPassword")
    fun resetPassword(@Body user: ResetPassword): Call<User>

    @GET("auth/token")
    fun token(@QueryMap params: Map<String, String>): Call<String>
}