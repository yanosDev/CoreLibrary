package de.yanos.data.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.yanos.core.utils.DebugInterceptor
import de.yanos.core.utils.DefaultDispatcher
import de.yanos.core.utils.IODispatcher
import de.yanos.core.utils.MainDispatcher
import de.yanos.data.BuildConfig
import de.yanos.data.api.AuthApi
import de.yanos.data.database.LibDatabase
import de.yanos.data.database.LibDatabaseImpl
import de.yanos.data.database.dao.UserDao
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal class DataModule {

    @Provides
    @DebugInterceptor
    fun provideDebugInterceptor(): Interceptor {
        return HttpLoggingInterceptor()
    }

    @Provides
    fun provideOkHttpClient(@DebugInterceptor debugInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG)
                    addInterceptor(debugInterceptor)
            }.build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }

    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    fun provideDB(@ApplicationContext context: Context): LibDatabase {
        return LibDatabaseImpl.db(context)
    }

    @Provides
    fun provideUserDao(db: LibDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    @IODispatcher
    fun provideIODispatcher() = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher() = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher() = Dispatchers.Default
}