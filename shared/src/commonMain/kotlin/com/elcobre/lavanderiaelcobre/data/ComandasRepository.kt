package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Pedido

class OperacionComandaException(message: String, cause: Throwable? = null) : Exception(message, cause)

interface ComandasRepository {
    suspend fun asignadas(): List<Pedido>
    suspend fun detalle(id: String): Pedido?
    suspend fun buscarNumero(numero: String): Pedido?
    suspend fun escanear(codigo: String): Pedido?
    suspend fun avanzar(pedido: Pedido)
    suspend fun comentar(id: String, texto: String)
    suspend fun alertar(id: String, motivo: String, texto: String)
    suspend fun resolver(pedido: Pedido)
    fun cerrarSesion()
}

class FirebaseComandasRepository : ComandasRepository {
    override suspend fun asignadas() = getMisComandasAsignadasFirebase()
    override suspend fun detalle(id: String) = getComandaDetalleFirebase(id)
    override suspend fun buscarNumero(numero: String) = buscarNumeroComandaFirebase(numero)
    override suspend fun escanear(codigo: String): Pedido? {
        val encontrado = buscarComandaPorQrFirebase(codigo) ?: return null
        autoAsignarComandaFirebase(encontrado.id)
        return detalle(encontrado.id)
            ?: throw OperacionComandaException("La asignación se guardó, pero no se pudo cargar la comanda. Reintenta.")
    }
    override suspend fun avanzar(pedido: Pedido) {
        val etapa = pedido.etapaIdDb ?: throw OperacionComandaException("La comanda no tiene una etapa activa.")
        val orden = pedido.ordenEtapaDb ?: throw OperacionComandaException("La comanda no tiene un flujo válido.")
        check(completarEtapaFirebase(pedido.id, etapa, orden, estadoTrasCompletar(orden)))
    }
    override fun cerrarSesion() = com.elcobre.lavanderiaelcobre.data.auth.AuthRepository().cerrarSesion()
    override suspend fun comentar(id: String, texto: String) = comentarComandaFirebase(id, texto)
    override suspend fun alertar(id: String, motivo: String, texto: String) = alertarComandaFirebase(id, motivo, texto)
    override suspend fun resolver(pedido: Pedido) = resolverComandaFirebase(pedido.id)
}

fun estadoTrasCompletar(orden: Int): String = when (orden) {
    in 1..3 -> "EN_PROCESO"
    4 -> "FINALIZADA"
    5 -> "ENTREGADA"
    else -> throw OperacionComandaException("Orden de etapa inválido.")
}
