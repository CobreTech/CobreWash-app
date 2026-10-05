package com.elcobre.lavanderiaelcobre.data

internal actual suspend fun consultarAvisosFirebase(offset: Int, limit: Int): PaginaAvisos =
    throw OperacionComandaException("Los avisos de Firebase aún no están disponibles en iOS.")
