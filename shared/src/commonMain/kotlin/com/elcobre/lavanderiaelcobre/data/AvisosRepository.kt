package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Aviso

data class PaginaAvisos(val avisos: List<Aviso>, val total: Int)

fun interface AvisosRepository {
    suspend fun consultar(offset: Int, limit: Int): PaginaAvisos
}

class FirebaseAvisosRepository : AvisosRepository {
    override suspend fun consultar(offset: Int, limit: Int): PaginaAvisos = consultarAvisosFirebase(offset, limit)
}

internal expect suspend fun consultarAvisosFirebase(offset: Int, limit: Int): PaginaAvisos
