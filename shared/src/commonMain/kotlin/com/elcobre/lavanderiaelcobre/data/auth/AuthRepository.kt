package com.elcobre.lavanderiaelcobre.data.auth

/** Resultado de un intento de autenticación contra Firebase Auth. */
sealed interface AuthResultado {
    data class Exito(val uid: String, val email: String) : AuthResultado
    data class Error(val mensaje: String) : AuthResultado
}

/**
 * Puerta de entrada a Firebase Authentication. Vive como expect/actual porque el
 * SDK oficial de Firebase Auth es Android-only: la actual de iOS es un stub hasta
 * que se decida su integración (ver LoginScreen/CobreViewModel).
 */
expect class AuthRepository() {
    suspend fun iniciarSesion(correo: String, password: String): AuthResultado
    fun cerrarSesion()
}
