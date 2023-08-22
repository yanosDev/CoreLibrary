package de.yanos.domain.api

import de.yanos.domain.client.YanosClientBuilder
import okhttp3.Interceptor
import retrofit2.Converter

interface AuthenticationApiBuilder {

    fun baseUrl(url: String): AuthenticationApiBuilder
    fun addConverterFactory(factory: Converter.Factory): AuthenticationApiBuilder
    fun addInterceptor(interceptor: Interceptor): AuthenticationApiBuilder
    fun build(): AuthenticationApi

    companion object {
        fun builder(): AuthenticationApiBuilder {
            return AuthenticationApiBuilderImpl()
        }
    }
}

internal class AuthenticationApiBuilderImpl : AuthenticationApiBuilder {
    private var url: String? = null
    private val factories: MutableList<Converter.Factory> = mutableListOf()
    private val interceptors: MutableList<Interceptor> = mutableListOf()

    override fun addConverterFactory(factory: Converter.Factory): AuthenticationApiBuilder {
        factories.add(factory)
        return this
    }

    override fun addInterceptor(interceptor: Interceptor): AuthenticationApiBuilder {
        this.interceptors.add(interceptor)
        return this
    }

    override fun baseUrl(url: String): AuthenticationApiBuilder {
        this.url = url
        return this
    }

    override fun build(): AuthenticationApi {
        if(url == null)
            throw RuntimeException("You need to provide a baseUrl")
        return YanosClientBuilder.builder().baseUrl(url!!).apply {
            factories.forEach { addConverterFactory(it) }
            interceptors.forEach { addInterceptor(it) }
        }.build().create(AuthenticationApi::class.java)
    }
}