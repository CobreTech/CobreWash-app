package com.elcobre.lavanderiaelcobre.data.auth

/** Perfil real traído desde Data Connect tras autenticar (mismo esquema que la web). */
sealed interface PerfilResultado {
    data class Exito(val nombre: String, val rolNombre: String) : PerfilResultado
    data class Error(val mensaje: String) : PerfilResultado
}

/**
 * Trae el perfil del usuario autenticado (nombre, rol) usando el SDK de Kotlin de
 * Data Connect generado desde el mismo esquema que usa la web (`GetMiPerfil`).
 * Android-only por ahora: ver [AuthRepository] para la razón del expect/actual.
 */
expect class PerfilRepository() {
    suspend fun obtenerMiPerfil(): PerfilResultado
}
