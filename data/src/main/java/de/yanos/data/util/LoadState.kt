package de.yanos.data.util

sealed interface LoadState<T> {
    data class Data<T>(val data: T) : LoadState<T>
    data class Failure<T>(val e: Exception) : LoadState<T>
}