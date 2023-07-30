package de.yanos.data.util

import android.content.Context
import de.yanos.data.BuildConfig
import de.yanos.data.api.AuthApi
import de.yanos.data.database.LibDatabaseImpl
import de.yanos.data.service.auth.AuthLocalSource
import de.yanos.data.service.auth.AuthLocalSourceImpl
import de.yanos.data.service.auth.AuthRemoteSourceImpl
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * Usually i would use proper DI but just for convenience created this class
 */
internal class LibDI {

    companion object {
        val dispatcher = Dispatchers.IO
        val okHttp = OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }
            .build()
        val retrofit = Retrofit
            .Builder()
            .client(okHttp)
            .baseUrl(BuildConfig.BASE_URL)
            .build()
        val authApi = retrofit.create(AuthApi::class.java)

        fun localAuthSource(context: Context): AuthLocalSource {
            return AuthLocalSourceImpl(dao = LibDatabaseImpl.db(context).userDao(), dispatcher = dispatcher)
        }

        val remote = AuthRemoteSourceImpl(api = authApi, dispatcher = dispatcher)
    }
}