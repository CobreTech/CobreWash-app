package com.elcobre.lavanderiaelcobre.data

/**
 * Busca el ID interno de una comanda escaneando su QR público en el servidor (Firebase).
 * Devuelve el ID de la comanda si existe y el operario tiene permisos, o null si falla.
 */
import com.elcobre.lavanderiaelcobre.data.model.Pedido

/**
 * Busca una comanda escaneando su QR público en el servidor (Firebase).
 * Devuelve el Pedido instanciado si existe, o null si falla.
 */
expect suspend fun buscarComandaPorQrFirebase(codigoQr: String): Pedido?

/**
 * Obtiene la lista de comandas paginadas/activas desde Firebase.
 */
expect suspend fun getComandasActivasFirebase(): List<Pedido>

expect suspend fun getMisComandasAsignadasFirebase(): List<Pedido>

expect suspend fun autoAsignarComandaFirebase(comandaId: String)

expect suspend fun completarEtapaFirebase(comandaId: String, etapaId: String, orden: Int, estadoComandaActual: String): Boolean

/**
 * Obtiene el detalle completo de un pedido desde Firebase dado su ID interno.
 */
expect suspend fun getComandaDetalleFirebase(id: String): Pedido?
expect suspend fun comentarComandaFirebase(id: String, texto: String)
expect suspend fun alertarComandaFirebase(id: String, motivo: String, texto: String)
expect suspend fun resolverComandaFirebase(id: String)
expect suspend fun buscarNumeroComandaFirebase(numero: String): Pedido?
