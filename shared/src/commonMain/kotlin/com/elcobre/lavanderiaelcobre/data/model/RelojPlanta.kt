package com.elcobre.lavanderiaelcobre.data.model

/**
 * Proveedor multiplataforma de la hora actual del día (0..23) para
 * la detección automática del turno operativo de planta.
 */
internal expect fun obtenerHoraActualDelDia(): Int

/**
 * Proveedor multiplataforma de la hora actual en formato "HH:mm".
 */
internal expect fun obtenerHoraFormateadaActual(): String
