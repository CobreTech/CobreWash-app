package com.elcobre.lavanderiaelcobre.data

internal actual suspend fun catalogoInsumosFirebase(): List<InsumoInventario> =
    throw OperacionComandaException("El inventario de Firebase aún no está disponible en iOS.")

internal actual suspend fun insumoPorQrFirebase(codigo: String): InsumoInventario? =
    throw OperacionComandaException("El inventario de Firebase aún no está disponible en iOS.")
