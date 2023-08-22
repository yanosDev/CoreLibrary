package de.yanos.data.model

import com.squareup.moshi.Moshi

interface PostBody {
    abstract fun toQueryMap(): Map<String, String>
}