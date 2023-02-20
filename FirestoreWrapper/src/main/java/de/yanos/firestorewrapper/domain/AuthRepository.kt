package de.yanos.firestorewrapper.domain

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher

interface AuthRepository {

}

internal class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val dispatcher: CoroutineDispatcher
) : AuthRepository {

}