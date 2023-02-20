package de.yanos.firestorewrapper.domain

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import de.yanos.crashlog.util.Clog
import de.yanos.firestorewrapper.util.Condition
import de.yanos.firestorewrapper.util.DatabasePath
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class DatabaseConfig(var isPersistenceEnabled: Boolean = false, var dispatcher: CoroutineDispatcher = Dispatchers.IO)

interface DatabaseRepositoryBuilder {
    fun enableOfflinePersistence()
    fun disableOfflinePersistence()
    fun build(): DatabaseRepository

    companion object {
        fun Builder(): DatabaseRepositoryBuilder {
            return DatabaseRepositoryBuilderImpl()
        }
    }
}

internal class DatabaseRepositoryBuilderImpl : DatabaseRepositoryBuilder {
    private val config = DatabaseConfig()

    override fun enableOfflinePersistence() {
        config.isPersistenceEnabled = true
    }

    override fun disableOfflinePersistence() {
        config.isPersistenceEnabled = false
    }

    override fun build(): DatabaseRepository {
        return DatabaseRepositoryImpl(config)
    }
}

interface DatabaseRepository {
    suspend fun <T> create(path: DatabasePath<T>, values: Map<String, Any>): StoreResult<T>
    suspend fun <T> read(path: DatabasePath<T>): StoreResult<T>
    suspend fun <T> readList(path: DatabasePath<T>): StoreResult<List<T>>
    suspend fun <T> subscribe(path: DatabasePath<T>): Flow<StoreResult<T>>
    suspend fun <T> subscribeList(path: DatabasePath<T>): Flow<StoreResult<List<T>>>
    suspend fun <T> update(path: DatabasePath<T>, values: Map<String, Any>): StoreResult<T>
    suspend fun <T> delete(path: DatabasePath<T>): StoreResult<T>
}

internal class DatabaseRepositoryImpl(config: DatabaseConfig) : DatabaseRepository {
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val dispatcher = config.dispatcher

    init {
        val settings = FirebaseFirestoreSettings.Builder().apply {
            isPersistenceEnabled = config.isPersistenceEnabled
        }.build()
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
    for (condition in conditions) query = when (condition) {
        is Condition.WhereEquals -> query.whereEqualTo(condition.field, condition.value)
        is Condition.WhereIn -> query.whereIn(condition.field, condition.values)
        is Condition.WhereGreaterThan -> query.whereGreaterThan(condition.field, condition.value)
        is Condition.WhereLessThan -> query.whereLessThan(condition.field, condition.value)
        is Condition.WhereArrayContains -> query.whereArrayContains(condition.field, condition.value)
        is Condition.OrderByAscending -> query.orderBy(condition.field, Query.Direction.ASCENDING)
        is Condition.OrderByDescending -> query.orderBy(condition.field, Query.Direction.DESCENDING)
        is Condition.Limit -> query.limit(condition.count)
    }
    return query
}

fun <T> FirebaseFirestore.update(path: DatabasePath<T>, values: Map<String, Any>): Task<Void> {
    return document(path.buildPath()).update(values)
}

fun <T> FirebaseFirestore.delete(path: DatabasePath<T>): Task<Void> {
    return document(path.buildPath()).delete()
}

fun <T> DatabasePath<T>.buildPath(): String {
    return path.joinToString(separator = "/")
}

sealed interface StoreResult<out T> {
    class Load<T>(data: T) : StoreResult<T>
    class Failure<T>(error: String?) : StoreResult<T>
    object Success : StoreResult<Nothing>
}