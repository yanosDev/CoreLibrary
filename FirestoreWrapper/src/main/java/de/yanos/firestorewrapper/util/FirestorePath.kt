package de.yanos.firestorewrapper.util

import de.yanos.firestorewrapper.domain.Condition

interface FirestorePath<T> {
    val id: String
}

internal data class FirestorePathImpl<T>(val path: List<String>, val conditions: MutableList<Condition>, val clazz: Class<T>) : FirestorePath<T> {
    override val id
        get() =
            "${path.joinToString(separator = "/")} ${
                conditions.sortedBy { it.priority }.joinToString { condition -> condition.priority.toString() + condition.uniqueId }
            }"
}

sealed interface FirestorePathBuilder<T> {
    val paths: MutableList<String>
    val conditions: MutableList<Condition>
    val clazz: Class<T>
    fun build(): FirestorePath<T> {
        return FirestorePathImpl(paths, conditions, clazz)
    }

    companion object {
        fun <T> Builder(clazz: Class<T>): CollectionPathBuilder<T> {
            return CollectionPathBuilderImpl<T>(mutableListOf(), mutableListOf(), clazz)
        }
    }
}

interface DocumentPathBuilder<T> : FirestorePathBuilder<T> {
    fun collection(path: String): CollectionPathBuilder<T>
}

interface CollectionPathBuilder<T> : FirestorePathBuilder<T> {
    fun document(path: String): DocumentPathBuilder<T>
    fun condition(condition: Condition): QueryPathBuilder<T>
}

interface QueryPathBuilder<T> : FirestorePathBuilder<T> {
    fun condition(condition: Condition): QueryPathBuilder<T>
}


class DocumentPathBuilderImpl<T>(
    override val paths: MutableList<String>,
    override val conditions: MutableList<Condition>,
    override val clazz: Class<T>
) : DocumentPathBuilder<T> {
    override fun collection(path: String): CollectionPathBuilder<T> {
        return CollectionPathBuilderImpl(paths.apply { add(path) }, conditions, clazz)
    }
}

class CollectionPathBuilderImpl<T>(
    override val paths: MutableList<String>,
    override val conditions: MutableList<Condition>,
    override val clazz: Class<T>
) : CollectionPathBuilder<T> {
    override fun document(path: String): DocumentPathBuilder<T> {
        return DocumentPathBuilderImpl(paths.apply { add(path) }, conditions, clazz)
    }

    override fun condition(condition: Condition): QueryPathBuilder<T> {
        return QueryPathBuilderImpl(paths, conditions.apply { add(condition) }, clazz)
    }
}

class QueryPathBuilderImpl<T>(
    override val paths: MutableList<String>,
    override val conditions: MutableList<Condition>,
    override val clazz: Class<T>
) : QueryPathBuilder<T> {
    override fun condition(condition: Condition): QueryPathBuilder<T> {
        return QueryPathBuilderImpl(paths, conditions.apply { add(condition) }, clazz)
    }
}