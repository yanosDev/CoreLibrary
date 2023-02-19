package de.yanos.firestorewrapper.util

import de.yanos.firestorewrapper.domain.Condition

class FirestorePath(val path: List<String>, conditions: MutableList<Condition>)

sealed class FirestorePathBuilder<T>(
    open val paths: MutableList<String> = mutableListOf(),
    open val conditions: MutableList<Condition> = mutableListOf()
) {
    fun build(): FirestorePath {
        return FirestorePath(paths, conditions)
    }

    class DocumentPathBuilder<T>(
        override val paths: MutableList<String>,
        override val conditions: MutableList<Condition>
    ) : FirestorePathBuilder<T>() {
        fun collection(path: String): CollectionPathBuilder<T> {
            return CollectionPathBuilder(paths.apply { add(path) }, conditions)
        }
    }

    class CollectionPathBuilder<T>(
        override val paths: MutableList<String>,
        override val conditions: MutableList<Condition>
    ) : FirestorePathBuilder<T>() {

        fun document(path: String): DocumentPathBuilder<T> {
            return DocumentPathBuilder(paths.apply { add(path) }, conditions)
        }

        fun condition(condition: Condition): QueryPathBuilder<T> {
            return QueryPathBuilder(paths, conditions.apply { add(condition) })
        }
    }

    class QueryPathBuilder<T>(
        override val paths: MutableList<String>,
        override val conditions: MutableList<Condition>
    ) : FirestorePathBuilder<T>() {
        fun condition(condition: Condition): QueryPathBuilder<T> {
            return QueryPathBuilder(paths, conditions.apply { add(condition) })
        }
    }
}