package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Pedido

actual suspend fun buscarComandaPorQrFirebase(codigoQr: String): Pedido? {
    // Aún no implementado en iOS
    return null
}

actual suspend fun getComandasActivasFirebase(): List<Pedido> {
    return emptyList()
}

actual suspend fun getComandaDetalleFirebase(id: String): Pedido? {
    return null
}

actual suspend fun getMisComandasAsignadasFirebase(): List<Pedido> = emptyList()
actual suspend fun autoAsignarComandaFirebase(comandaId: String): Unit =
    throw OperacionComandaException("El seguimiento de Firebase todavía no está disponible en iOS.")


actual suspend fun completarEtapaFirebase(comandaId: String, etapaId: String, orden: Int, estadoComandaActual: String): Boolean = false
actual suspend fun comentarComandaFirebase(id: String, texto: String): Unit =
    throw OperacionComandaException("Firebase no está disponible en iOS.")
actual suspend fun alertarComandaFirebase(id: String, motivo: String, texto: String): Unit =
    throw OperacionComandaException("Firebase no está disponible en iOS.")
actual suspend fun resolverComandaFirebase(id: String): Unit =
    throw OperacionComandaException("Firebase no está disponible en iOS.")
actual suspend fun buscarNumeroComandaFirebase(numero: String): Pedido? =
    throw OperacionComandaException("Firebase no está disponible en iOS.")

