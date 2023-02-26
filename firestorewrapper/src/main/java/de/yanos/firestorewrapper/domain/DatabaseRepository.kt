package de.yanos.firestorewrapper.domain

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import de.yanos.crashlog.util.Clog
import de.yanos.firestorewrapper.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


interface DatabaseRepositoryBuilder {
    fun enableOfflinePersistence(): DatabaseRepositoryBuilder
    fun disableOfflinePersistence(): DatabaseRepositoryBuilder
    fun setDispatcher(dispatcher: CoroutineDispatcher): DatabaseRepositoryBuilder
    fun build(): DatabaseRepository

    companion object {
        fun Builder(): DatabaseRepositoryBuilder {
            return DatabaseRepositoryBuilderImpl()
        }
    }
}

private class DatabaseRepositoryBuilderImpl : DatabaseRepositoryBuilder {
    private var isPersistenceEnabled = true
    private var dispatcher: CoroutineDispatcher? = null
    override fun enableOfflinePersistence(): DatabaseRepositoryBuilder {
        isPersistenceEnabled = true
        return this
    }

    override fun disableOfflinePersistence(): DatabaseRepositoryBuilder {
        isPersistenceEnabled = false
        return this
    }

    override fun setDispatcher(dispatcher: CoroutineDispatcher): DatabaseRepositoryBuilder {
        this.dispatcher = dispatcher
        return this
    }

    override fun build(): DatabaseRepository {
        return DatabaseRepositoryImpl(isPersistenceEnabled, dispatcher)
    }
}

interface DatabaseRepository {
    suspend fun <T> create(path: DatabasePath<T>, values: Map<String, Any>): StoreResult<T>
    suspend fun <T> read(path: DatabasePath<T>): StoreResult<T>
    suspend fun <T> readList(path: DatabasePath<T>): StoreResult<List<T>>
    suspend fun <T> subscribe(path: DatabasePath<T>): Flow<StoreResult<T>>
    suspend fun <T> subscribeList(path: DatabasePath<T>): Flow<StoreResult<List<T>>>
    suspend fun <T> subscribeChanges(path: DatabasePath<T>): Flow<StoreResult<List<T>>>
    suspend fun <T> paginateList(
        path: CollectionPathBuilder<T>,
        key: Long?,
        isPreviousLoad: Boolean,
        orderBy: String,
        limit: Long
    ): StoreResult.Load<Pair<List<T>, PageKey>>

    suspend fun <T> update(path: DatabasePath<T>, values: Map<String, Any>): StoreResult<T>
    suspend fun <T> delete(path: DatabasePath<T>): StoreResult<T>
}

private class DatabaseRepositoryImpl(isPersistenceEnabled: Boolean, cd: CoroutineDispatcher? = null) : DatabaseRepository {
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val dispatcher: CoroutineDispatcher

    init {
        dispatcher = cd ?: Dispatchers.IO
        val settings = FirebaseFirestoreSettings.Builder().apply { this.isPersistenceEnabled = isPersistenceEnabled }.build()
        store.firestoreSettings = settings
    }

    override suspend fun <T> create(path: DatabasePath<T>, values: Map<String, Any>): StoreResult<T> {
        return withContext(dispatcher) {
            suspendCoroutine { cont ->
                if (path.isCollectionRequest())
                    cont.resume(StoreResult.Failure("The given path is wrong for"))
                store.create(path, values)
                    .addOnSuccessListener { cont.resume(StoreResult.Success) }
                    .addOnFailureListener {
                        Clog.e(it.stackTraceToString())
                        cont.resume(StoreResult.Failure(it.localizedMessage))
                    }
            }
        }
    }

    override suspend fun <T> read(path: DatabasePath<T>): StoreResult<T> {
        return withContext(dispatcher) {
            suspendCoroutine { cont ->
                store.read(path)
                    .get()
                    .addOnSuccessListener {
                        cont.resume(it.takeIf { it.exists() }
                            ?.toObject(path.clazz)
                            ?.let { document -> StoreResult.Load(document) }
                            ?: StoreResult.Failure("Document not found")
                        )
                    }
                    .addOnFailureListener {
                        Clog.e(it.stackTraceToString())
                        cont.resume(StoreResult.Failure(it.localizedMessage))
                    }
            }
        }
    }

    override suspend fun <T> readList(path: DatabasePath<T>): StoreResult<List<T>> {
        return withContext(dispatcher) {
            suspendCoroutine { cont ->
                store.readAll(path)
                    .get()
                    .addOnSuccessListener {
                        cont.resume(it.takeIf { it.documents.isNotEmpty() }
                            ?.toObjects(path.clazz)
                            ?.let { documents -> StoreResult.Load(documents) }
                            ?: StoreResult.Failure("Documents not found")
                        )
                    }
                    .addOnFailureListener {
                        Clog.e(it.stackTraceToString())
                        cont.resume(StoreResult.Failure(it.localizedMessage))
                    }
            }
        }
    }

    override suspend fun <T> subscribe(path: DatabasePath<T>): Flow<StoreResult<T>> {
        return withContext(dispatcher) {
            callbackFlow {
                val subscriber = store.read(path).addSnapshotListener { snapshot, error ->
                    val result = when {
                        error != null -> StoreResult.Failure(error.stackTraceToString())
                        snapshot?.exists() != true -> StoreResult.Failure("Document not found")
                        else -> {
                            snapshot
                                .toObject(path.clazz)
                                ?.let { documents -> StoreResult.Load(documents) }
                                ?: StoreResult.Failure("Parsing model failed")
                        }
                    }
                    trySend(result)
                }
                awaitClose {
                    subscriber.remove()
                }
            }
        }
    }

    override suspend fun <T> subscribeList(path: DatabasePath<T>): Flow<StoreResult<List<T>>> {
        return withContext(dispatcher) {
            callbackFlow {
                val subscriber = store.readAll(path).addSnapshotListener { snapshot, error ->
                    val result = when {
                        error != null -> StoreResult.Failure(error.stackTraceToString())
                        snapshot?.documents?.isNotEmpty() != true -> StoreResult.Failure("Document not found")
                        else -> snapshot
                            .toObjects(path.clazz)
                            .let { documents -> StoreResult.Load(documents) }
                    }
                    trySend(result)
                }
                awaitClose {
                    subscriber.remove()
                }
            }
        }
    }

    override suspend fun <T> subscribeChanges(path: DatabasePath<T>): Flow<StoreResult<List<T>>> {
        return withContext(dispatcher) {
            callbackFlow {
                val subscriber = store.readAll(path).addSnapshotListener { snapshot, error ->
                    val result = when {
                        error != null -> StoreResult.Failure(error.stackTraceToString())
                        snapshot?.documentChanges?.isNotEmpty() != true -> StoreResult.Failure("Document not found")
                        else -> snapshot.documentChanges
                            .map { documentChange -> documentChange.document.toObject(path.clazz) }
                            .let { documents -> StoreResult.Load(documents) }
                    }
                    trySend(result)
                }
                awaitClose {
                    subscriber.remove()
                }
            }
        }
    }

    override suspend fun <T> paginateList(
        path: CollectionPathBuilder<T>,
        key: Long?,
        isPreviousLoad: Boolean,
        orderBy: String,
        limit: Long
    ): StoreResult.Load<Pair<List<T>, PageKey>> {
        return withContext(dispatcher) {
            suspendCoroutine { cont ->
                val errorLoad: StoreResult.Load<Pair<List<T>, PageKey>> = StoreResult.Load(Pair(listOf(), PageKeyImpl(null, 0L, 0L)))
                store.readAll(path.apply {
                    key?.let { ts ->
                        if (isPreviousLoad)
                            condition(Condition.EndBefore(ts))
                        else condition(Condition.EndBefore(ts))
                    }
                }
                    .condition(Condition.OrderByDescending(orderBy))
                    .condition(Condition.Limit(limit)).build()
                )
                    .get()
                    .addOnSuccessListener {
                        cont.resume(it.takeIf { it.documents.isNotEmpty() }
                            ?.toObjects(path.clazz)
                            ?.let { documents ->
                                StoreResult.Load(
                                    Pair(
                                        documents,
                                        PageKeyImpl(
                                            it.documents.lastOrNull(),
                                            it.documents.firstOrNull()?.get("ts") as? Long ?: 0L,
                                            it.documents.lastOrNull()?.get("ts") as? Long ?: 0L
                                        )
                                    )
                                )
                            }
                            ?: errorLoad
                        )
                    }
                    .addOnFailureListener {
                        Clog.e(it.stackTraceToString())
                        cont.resume(errorLoad)
                    }
            }
        }
    }

    override suspend fun <T> update(path: DatabasePath<T>, values: Map<String, Any>): StoreResult<T> {
        return withContext(dispatcher) {
            suspendCoroutine { cont ->
                if (path.isCollectionRequest())
                    cont.resume(StoreResult.Failure("The given path is wrong for a document"))
                store.update(path, values)
                    .addOnSuccessListener { cont.resume(StoreResult.Success) }
                    .addOnFailureListener {
                        Clog.e(it.stackTraceToString())
                        cont.resume(StoreResult.Failure(it.localizedMessage))
                    }
            }
        }
    }

    override suspend fun <T> delete(path: DatabasePath<T>): StoreResult<T> {
        return withContext(dispatcher) {
            suspendCoroutine { cont ->
                if (path.isCollectionRequest())
                    cont.resume(StoreResult.Failure("The given path is wrong for a document"))
                store.delete(path)
                    .addOnSuccessListener { cont.resume(StoreResult.Success) }
                    .addOnFailureListener {
                        Clog.e(it.stackTraceToString())
                        cont.resume(StoreResult.Failure(it.localizedMessage))
                    }
            }
        }
    }
}

fun <T> FirebaseFirestore.create(path: DatabasePath<T>, values: Map<String, Any>): Task<Void> {
    return document(path.buildPath()).set(values)
}

fun <T> FirebaseFirestore.read(path: DatabasePath<T>): DocumentReference {
    return document(path.buildPath())
}

fun <T> FirebaseFirestore.readAll(path: DatabasePath<T>): Query {
    return collection(path.buildPath()).buildConditions(path.conditions)
}

fun Query.buildConditions(conditions: List<Condition>): Query {
    var query = this
    try {
        for (condition in conditions.sortedBy { it.priority })
            query = when (condition) {
                is Condition.WhereEquals -> query.whereEqualTo(condition.field, condition.value)
                is Condition.WhereIn -> query.whereIn(condition.field, condition.values)
                is Condition.WhereGreaterThan -> query.whereGreaterThan(condition.field, condition.value)
                is Condition.WhereLessThan -> query.whereLessThan(condition.field, condition.value)
                is Condition.WhereArrayContains -> query.whereArrayContains(condition.field, condition.value)
                is Condition.OrderByAscending -> query.orderBy(condition.field, Query.Direction.ASCENDING)
                is Condition.OrderByDescending -> query.orderBy(condition.field, Query.Direction.DESCENDING)
                is Condition.Limit -> query.limit(condition.count)
                is Condition.EndAt -> query.endAt(condition.value)
                is Condition.EndBefore -> query.endBefore(condition.value)
                is Condition.StartAfter -> query.startAfter(condition.value)
                is Condition.StartAt -> query.startAt(condition.value)
            }
    } catch (e: Exception) {
        Clog.e(e.stackTraceToString())
    }
    return query
}

fun <T> FirebaseFirestore.update(path: DatabasePath<T>, values: Map<String, Any>): Task<Void> {
    return document(path.buildPath()).update(values.replaceEdits())
}

fun <T> FirebaseFirestore.delete(path: DatabasePath<T>): Task<Void> {
    return document(path.buildPath()).delete()
}

fun <T> DatabasePath<T>.buildPath(): String {
    return path.joinToString(separator = "/")
}

fun Map<String, Any>.replaceEdits(): Map<String, Any> {
    val newMap = mutableMapOf<String, Any>()
    forEach { (field, value) ->
        newMap[field] = {
            when (value) {
                is FieldEdit.Delete -> FieldValue.delete()
                is FieldEdit.ArrayAdd -> FieldValue.arrayUnion(value.ids)
                is FieldEdit.ArrayRemove -> FieldValue.arrayRemove(value.ids)
                else -> value
            }
        }
    }
    return newMap
}

sealed interface StoreResult<out T> {
    class Load<T>(val data: T) : StoreResult<T>
    class Failure<T>(val error: String?) : StoreResult<T>
    object Success : StoreResult<Nothing>
}

interface PageKey {
    val startTs: Long
    val endTs: Long
}

data class PageKeyImpl(val documentSnapshot: DocumentSnapshot?, override val startTs: Long, override val endTs: Long) : PageKey