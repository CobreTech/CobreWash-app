package com.elcobre.lavanderiaelcobre.data

data class InsumoInventario(
    val id: String,
    val codigoQr: String,
    val nombre: String,
    val unidad: String,
    val stockActual: Double,
    val stockMinimo: Double,
    val activo: Boolean,
) {
    val estado: String get() = when {
        !activo -> "Inactivo"
        stockMinimo > 0 && stockActual <= stockMinimo * 0.5 -> "Crítico"
        stockMinimo > 0 && stockActual <= stockMinimo -> "Bajo"
        else -> "OK"
    }
}

/** Resultado del conteo físico en bodega contrastado con el stock del sistema (RF-IN10). */
data class ConteoFisico(
    val insumoId: String,
    val cantidadContada: Double,
    val stockSistema: Double,
) {
    val diferencia: Double get() = cantidadContada - stockSistema
    val coincide: Boolean get() = kotlin.math.abs(diferencia) < 0.001
    val estadoConteo: String
        get() = when {
            coincide -> "Cuadrado"
            diferencia < 0 -> "Faltante"
            else -> "Sobrante"
        }
}

interface InventarioRepository {
    suspend fun catalogo(): List<InsumoInventario>
    suspend fun porQr(codigo: String): InsumoInventario?
}

class FirebaseInventarioRepository : InventarioRepository {
    override suspend fun catalogo(): List<InsumoInventario> = catalogoInsumosFirebase()
    override suspend fun porQr(codigo: String): InsumoInventario? = insumoPorQrFirebase(codigo)
}

internal expect suspend fun catalogoInsumosFirebase(): List<InsumoInventario>
internal expect suspend fun insumoPorQrFirebase(codigo: String): InsumoInventario?
