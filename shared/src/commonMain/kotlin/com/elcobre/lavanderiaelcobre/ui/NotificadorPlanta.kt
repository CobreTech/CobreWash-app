package com.elcobre.lavanderiaelcobre.ui

/**
 * Canal de notificaciones del sistema para avisos de administración y nuevas
 * comandas asignadas al operario en planta (RF-AN02 / RF-AN04).
 */
internal expect fun notificarEventoPlanta(titulo: String, mensaje: String)
