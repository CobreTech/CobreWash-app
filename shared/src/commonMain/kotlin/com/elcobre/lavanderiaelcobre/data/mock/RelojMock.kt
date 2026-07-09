package com.elcobre.lavanderiaelcobre.data.mock

/**
 * Reloj simulado para el prototipo: entrega horas "HH:MM" incrementales sin
 * depender de un reloj de plataforma. Cada evento avanza unos minutos.
 */
object RelojMock {
    private var minutos = (13 * 60) + 45 // 13:45

    fun ahora(): String {
        minutos += 1
        val h = (minutos / 60) % 24
        val m = minutos % 60
        val hh = if (h < 10) "0$h" else h.toString()
        val mm = if (m < 10) "0$m" else m.toString()
        return "$hh:$mm"
    }
}
