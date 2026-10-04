package com.elcobre.lavanderiaelcobre.data.auth

/** Data Connect aún no está integrado en iOS (ver [AuthRepository]). */
actual class PerfilRepository actual constructor() {
    actual suspend fun obtenerMiPerfil(): PerfilResultado =
        PerfilResultado.Error("El perfil aún no está disponible en iOS.")
}
