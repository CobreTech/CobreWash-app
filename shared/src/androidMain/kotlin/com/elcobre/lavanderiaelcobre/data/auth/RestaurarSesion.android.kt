package com.elcobre.lavanderiaelcobre.data.auth

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

actual suspend fun restaurarPerfilAutenticado(): PerfilResultado? {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser ?: return null
    return try {
        user.getIdToken(true).await()
        if (auth.currentUser?.uid != user.uid) null else PerfilRepository().obtenerMiPerfil()
    } catch (_: FirebaseException) {
        PerfilResultado.Error("No se pudo validar tu sesión. Conéctate e inicia sesión nuevamente.")
    }
}
