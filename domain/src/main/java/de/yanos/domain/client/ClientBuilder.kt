package de.yanos.domain.client

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit

interface YanosClientBuilder {
    fun baseUrl(url: String): YanosClientBuilder
    fun addConverterFactory(factory: Converter.Factory): YanosClientBuilder
    fun addInterceptor(interceptor: Interceptor): YanosClientBuilder
    fun build(): YanosClient

    companion object {
        fun builder(): YanosClientBuilder = YanosClientBuilderImpl()
    }
}

internal class YanosClientBuilderImpl : YanosClientBuilder {
    private var url: String? = null
    private val factories: MutableList<Converter.Factory> = mutableListOf()
    private val interceptors: MutableList<Interceptor> = mutableListOf()

    override fun addConverterFactory(factory: Converter.Factory): YanosClientBuilder {
        factories.add(factory)
        return this
    }

    override fun addInterceptor(interceptor: Interceptor): YanosClientBuilder {
        this.interceptors.add(interceptor)
        return this
    }

    override fun baseUrl(url: String): YanosClientBuilder {
        this.url = url
        return this
    }

    override fun build(): YanosClient {
        if (url == null)
            throw RuntimeException("Client Configuration Error. Please make sure to provide all necessary Data")
        else return YanosClientImpl(url!!, interceptors, factories)
    }

}

interface YanosClient {
    fun <T> create(clazz: Class<T>): T
}

internal class YanosClientImpl(
    private val baseUrl: String,
    private val interceptors: List<Interceptor>,
    private val factories: List<Converter.Factory>
) : YanosClient {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .apply {
            factories.forEach { addConverterFactory(it) }
        }
        .client(OkHttpClient.Builder().retryOnConnectionFailure(true).apply { interceptors.forEach { addInterceptor(it) } }.build())
        .build()

    override fun <T> create(clazz: Class<T>): T = retrofit.create(clazz)

    companion object {
        val default by lazy {
            YanosClientImpl(
                "",
                listOf(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }),
                listOf()
            )
        }
    }
}