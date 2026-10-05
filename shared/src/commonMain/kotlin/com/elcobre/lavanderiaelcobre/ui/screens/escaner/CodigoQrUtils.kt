package com.elcobre.lavanderiaelcobre.ui.screens.escaner

/**
 * Validar y extraer el UUID de un QR de la comanda.
 * Rechazar HTTP, dominios parecidos, credenciales, otra ruta, parámetro vacío o duplicado.
 */
fun extraerCodigoQr(raw: String): String? {
    val prefix = "https://lavanderia-elcobre.vercel.app/seguimiento?qr="
    if (raw.startsWith(prefix)) {
        val uuid = raw.substringAfter(prefix).trim()
        if (uuid.matches(Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"))) {
            return uuid
        }
    }
    return null
}
