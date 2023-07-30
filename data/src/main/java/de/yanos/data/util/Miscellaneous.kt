package de.yanos.data.util

import com.squareup.moshi.Moshi

internal fun toQueryMap(obj: Any): Map<String, String> {
    val moshi: Moshi = Moshi.Builder().build()
    val adapter = moshi.adapter(Any::class.java)
    return adapter.toJsonValue(obj) as Map<String, String>
}