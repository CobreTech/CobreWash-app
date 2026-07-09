package com.elcobre.lavanderiaelcobre.ui

/** Destinos de navegación del prototipo. Detalle navega por id. */
sealed interface Pantalla {
    data object Login : Pantalla
    data object Dashboard : Pantalla
    data object Avisos : Pantalla
    data object Escaner : Pantalla
    data object Insumos : Pantalla
    data object Configuracion : Pantalla
    data class Detalle(val pedidoId: String) : Pantalla
    data object Vehiculos : Pantalla
}

/** Orden relativo de cada destino, usado para decidir la dirección del slide de [CobreApp]. */
internal fun orden(p: Pantalla): Int = when (p) {
    Pantalla.Login -> 0
    Pantalla.Dashboard -> 1
    Pantalla.Vehiculos -> 1
    Pantalla.Avisos -> 1
    Pantalla.Escaner -> 2
    Pantalla.Insumos -> 2
    Pantalla.Configuracion -> 2
    is Pantalla.Detalle -> 3
}
