package de.yanos.firestorewrapper.util

interface DatabasePath<T> {
    val id: String
    val path: List<String>
    val conditions: List<Condition>
    val clazz: Class<T>
    fun isDocumentRequest(): Boolean
    fun isCollectionRequest(): Boolean
}

internal data class DatabasePathImpl<T>(
    override val path: List<String>,
    override val conditions: List<Condition>,
    override val clazz: Class<T>
) :
    DatabasePath<T> {
    override val id
        get() =
            "${path.joinToString(separator = "/")} ${
                conditions.sortedBy { it.priority }.joinToString { condition -> condition.priority.toString() + condition.uniqueId }
            }"

    override fun isDocumentRequest(): Boolean = path.size % 2 != 1
    override fun isCollectionRequest(): Boolean = !isDocumentRequest()
}

sealed interface DatabasePathBuilder<T> {
    val paths: List<String>
    val conditions: List<Condition>
    val clazz: Class<T>

    fun build(): DatabasePath<T> {
        return DatabasePathImpl(paths, conditions, clazz)
    }

    companion object {
        fun <T> Builder(collectionName: String, clazz: Class<T>): CollectionPathBuilder<T> {
            return CollectionPathBuilderImpl(mutableListOf(collectionName), mutableListOf(), clazz)
        }
    }
}

interface DocumentPathBuilder<T> : DatabasePathBuilder<T> {
    fun collection(path: String): CollectionPathBuilder<T>
}

interface CollectionPathBuilder<T> : DatabasePathBuilder<T> {
    fun document(path: String): DocumentPathBuilder<T>
    fun condition(condition: Condition): QueryPathBuilder<T>
}

interface QueryPathBuilder<T> : DatabasePathBuilder<T> {
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