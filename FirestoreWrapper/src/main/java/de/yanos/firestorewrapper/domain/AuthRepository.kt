package de.yanos.firestorewrapper.domain

import com.google.firebase.auth.*
import de.yanos.crashlog.util.Clog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class AuthConfig(var debugVerification: Boolean = false, var dispatcher: CoroutineDispatcher = Dispatchers.IO)

interface AuthRepositoryBuilder {
    fun enableDebugVerification(): AuthRepositoryBuilder
    fun disableDebugVerification(): AuthRepositoryBuilder
    fun build(): AuthRepository

    companion object {
        fun Builder(): AuthRepositoryBuilder {
            return AuthRepositoryBuilderImpl()
        }
    }
}

internal class AuthRepositoryBuilderImpl : AuthRepositoryBuilder {
    private val config = AuthConfig()
    override fun enableDebugVerification(): AuthRepositoryBuilder {
        config.debugVerification = true
        return this
    }

    override fun disableDebugVerification(): AuthRepositoryBuilder {
        config.debugVerification = false
        return this
    }

    override fun build(): AuthRepository {
        return AuthRepositoryImpl(config)
    }
}

interface AuthRepository {
    suspend fun isLoggedIn(): Boolean
    suspend fun userIsRegistered(email: String): Boolean
    suspend fun logOutUser()
    suspend fun signInAnonymously(): AuthResult
    suspend fun switchAnonymousToPassword(email: String, password: String): AuthResult
    suspend fun switchAnonymousToGoogle(idToken: String): AuthResult
    suspend fun createPasswordUser(email: String, password: String): AuthResult
    suspend fun loginPasswordUser(email: String, password: String): AuthResult
    suspend fun loginWithCredential(credential: AuthCredential): AuthResult
    suspend fun sendPasswordResetEmail(email: String): AuthResult
}

internal class AuthRepositoryImpl(config: AuthConfig) : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dispatcher = config.dispatcher

    init {
        auth.firebaseAuthSettings
            .setAppVerificationDisabledForTesting(!config.debugVerification)
    }

    override suspend fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun userIsRegistered(email: String): Boolean {
        return withContext(dispatcher) {
            try {
                auth.fetchSignInMethodsForEmail(email).await().signInMethods.isNullOrEmpty()
            } catch (e: FirebaseAuthException) {
                Clog.e(e.localizedMessage ?: "")
                false
            }
        }
    }

    override suspend fun logOutUser() {
        auth.signOut()
    }

    override suspend fun signInAnonymously(): AuthResult {
        return withContext(dispatcher) {
            try {
                auth.signInAnonymously().await()?.let { authResult ->
                    authResult.user?.let { user ->
                        AuthResult.SignIn(
                            id = user.uid,
                            email = user.email,
                            name = user.displayName,
                            provider = authResult.credential?.provider
                        )
                    }
                } ?: AuthResult.Failure("Anonymous login failed")
            } catch (e: FirebaseAuthException) {
                Clog.e(e.localizedMessage ?: "")
                AuthResult.Failure("Anonymous login failed")
            }
        }
    }

    override suspend fun switchAnonymousToPassword(email: String, password: String): AuthResult {
        return withContext(dispatcher) {
            try {
                linkAnonymousUser(EmailAuthProvider.getCredential(email, password))
            } catch (e: FirebaseAuthException) {
                Clog.e(e.localizedMessage ?: "")
                AuthResult.Failure("Anonymous login with email password failed")
            }
        }
    }

    override suspend fun switchAnonymousToGoogle(idToken: String): AuthResult {
        return withContext(dispatcher) {
            try {
                linkAnonymousUser(GoogleAuthProvider.getCredential(idToken, null))
            } catch (e: FirebaseAuthException) {
                Clog.e(e.localizedMessage ?: "")
                AuthResult.Failure("Anonymous login with credentials failed")
            }
        }
    }

    override suspend fun createPasswordUser(email: String, password: String): AuthResult {
        return withContext(dispatcher) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()?.let { authResult ->
                    authResult.user?.let { user ->
                        AuthResult.SignIn(id = user.uid, email = user.email, name = user.displayName, provider = authResult.credential?.provider)
                    }
                } ?: AuthResult.Failure("Creating user failed")
            } catch (e: FirebaseAuthException) {
                Clog.e(e.localizedMessage ?: "")
                AuthResult.Failure("Exception while email user creation")
            }
        }
    }

    override suspend fun loginPasswordUser(email: String, password: String): AuthResult {
        return withContext(dispatcher) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()?.let { authResult ->
                    authResult.user?.let { user ->
                        AuthResult.SignIn(id = user.uid, email = user.email, name = user.displayName, provider = authResult.credential?.provider)
                    }
                } ?: AuthResult.Failure("Login failed")
            } catch (e: FirebaseAuthException) {
                Clog.e(e.localizedMessage ?: "")
                AuthResult.Failure("Exception while password login")
            }
        }
    }

    override suspend fun loginWithCredential(credential: AuthCredential): AuthResult {
        return withContext(dispatcher) {
            try {
                auth.signInWithCredential(credential).await()?.user?.let { user ->
                    AuthResult.SignIn(id = user.uid, email = user.email, name = user.displayName, provider = credential.provider)
                } ?: AuthResult.Failure("Login failed")
            } catch (e: FirebaseAuthException) {
                Clog.e(e.localizedMessage ?: "")
                AuthResult.Failure("Exception while credential login")
            }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return withContext(dispatcher) {
            try {
                auth.sendPasswordResetEmail(email).apply { await() }.let {
                    if (it.isSuccessful)
                        AuthResult.PasswordResetSent
                    else AuthResult.Failure(it.exception?.localizedMessage)
                }
            } catch (e: FirebaseAuthException) {
                Clog.e(e.localizedMessage ?: "")
                AuthResult.Failure("Exception while Password reset")
            }
        }
    }

    private suspend fun linkAnonymousUser(credential: AuthCredential): AuthResult {
        return auth.currentUser?.linkWithCredential(credential)?.await()?.user?.let { user ->
            AuthResult.SignIn(id = user.uid, email = user.email, name = user.displayName, provider = credential.provider)
        } ?: AuthResult.Failure("Failed to link anonymous user")
    }
}

sealed interface AuthResult {
    object LoggedOut : AuthResult
    class SignIn(val id: String, val email: String?, val name: String?, val provider: String?) : AuthResult
    class Failure(error: String?) : AuthResult
    object PasswordResetSent : AuthResult
}